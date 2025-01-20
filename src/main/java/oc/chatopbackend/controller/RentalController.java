package oc.chatopbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oc.chatopbackend.dto.RentalDto;
import oc.chatopbackend.dto.RentalUpdateDto;
import oc.chatopbackend.entity.RentalEntity;
import oc.chatopbackend.entity.UserEntity;
import oc.chatopbackend.model.ErrorResponseModel;
import oc.chatopbackend.model.RentalModel;
import oc.chatopbackend.service.RentalService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rentals")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "RENTALS", description = "Endpoints for rentals opérations")
public class RentalController {

    private final RentalService rentalService;
    private final ModelMapper modelMapper;

    public RentalEntity convertToEntity(RentalDto rentalDto) {
        return modelMapper.map(rentalDto, RentalEntity.class);
    }

    public RentalModel convertToModel(RentalEntity rentalEntity) {
        RentalModel rentalModel = modelMapper.map(rentalEntity, RentalModel.class);
        // add ownerId to the model
        rentalModel.setOwner_id(rentalEntity.getUser().getId());
        return rentalModel;
    }

    @GetMapping
    @Operation(summary = "Get every rentals", description = "Returns an array of all rentals", security =
    @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Rentals are sent with success", content =
    @Content(mediaType = "application/json", schema = @Schema(example = "{\"rentals\":[{\"id\":1,\"name\":\"Location " +
            "1\"," + "\"surface\":50,\"price\":500,\"description\":\"Une belle location\"," + "\"picture\":\"http" +
            "://myPath...\"}]}"))), @ApiResponse(responseCode = "401", description = "Unauthorized", content =
    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseModel.class))),})
    public ResponseEntity<?> getAllRentals() {
        try {
            List<RentalEntity> rentalEntities = rentalService.getAllRentals();

            List<RentalModel> rentals = rentalEntities.stream().map(this::convertToModel).toList();
            return ResponseEntity.ok(Map.of("rentals", rentals));

        } catch (Exception e) {
            ErrorResponseModel errorResponse = new ErrorResponseModel(HttpStatus.NOT_FOUND.value(), e.getMessage());
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a rental by ID", description = "Retrieve a specific rental from the database by its " +
            "unique ID", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Rental found and sent in response", content =
    @Content(mediaType = "application/json", schema = @Schema(implementation = RentalModel.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType =
                    "application/json", schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "Rental not found in DB", content = @Content(mediaType =
                    "application/json", schema = @Schema(implementation = ErrorResponseModel.class)))})
    public ResponseEntity<?> getRentalById(@PathVariable Long id) {
        try {
            RentalEntity rentalEntity = rentalService.getRentalById(id);
            if (rentalEntity == null) {
                throw new Exception("Rental not found with ID: " + id);
            }
            RentalModel rentalModel = convertToModel(rentalEntity);
            return ResponseEntity.ok(rentalModel);

        } catch (Exception e) {
            ErrorResponseModel errorResponse = new ErrorResponseModel(HttpStatus.NOT_FOUND.value(), e.getMessage());
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        }
    }


    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "Post a new rental", description = "Save a new rental in the database and save the picture " +
            "in the server", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Rental created with success", content =
    @Content(mediaType = "application/json", schema = @Schema(example = "\"Rental created " + "!\""))),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(mediaType =
                    "application/json", schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType =
                    "application/json", schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "500", description = "Error on rental creation", content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseModel.class)))})
    public ResponseEntity<?> createRental(HttpServletRequest request, @ModelAttribute RentalDto rentalDto) {
        try {
            UserEntity reqUser = (UserEntity) request.getAttribute("user");
            MultipartFile pictureFile = rentalDto.getPicture();
            if (pictureFile != null && !pictureFile.isEmpty()) {
                RentalEntity rentalEntity = convertToEntity(rentalDto);
                String picturePath = savePictureToGetPath(pictureFile);
                rentalEntity.setPicture(picturePath);
                rentalEntity.setUser(reqUser);
                RentalEntity rentalEntitySaved = rentalService.saveRental(rentalEntity);
                if (rentalEntitySaved.getId() != null) {
                    return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Rental created !"));
                } else {
                    log.warn("something went bad on rental creation");
                    throw new Exception("rental creation failed");
                }
            } else {
                throw new Exception("image cannot be saved");
            }
        } catch (Exception e) {
            String error = e.getMessage();
            int httpCode = HttpStatus.BAD_REQUEST.value();
            if (error.equals("rental creation failed")) {
                httpCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            }
            ErrorResponseModel errorResponse = new ErrorResponseModel(httpCode, error);
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        }
    }

    @PutMapping(path = "/{rentalId}", consumes = "multipart/form-data")
    @Operation(summary = "Put a rental by ID", description = "Update a rental properties in the database", security =
    @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Rental updated with success", content =
    @Content(mediaType = "application/json", schema = @Schema(example = "{\"message" + "\":\"Rental updated !\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(mediaType =
                    "application/json", schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType =
                    "application/json", schema = @Schema(implementation = ErrorResponseModel.class))),
            @ApiResponse(responseCode = "404", description = "The rental to update is not found in DB", content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseModel.class)))})
    public ResponseEntity<?> updateRental(HttpServletRequest request, @ModelAttribute RentalUpdateDto rentalDto,
            @PathVariable Long rentalId) {
        try {
            UserEntity reqUser = (UserEntity) request.getAttribute("user");
            if (rentalId == null) {
                throw new Exception("Rental ID is required for update");
            }
            RentalEntity existingRental = rentalService.getRentalById(rentalId);
            if (existingRental == null) {
                throw new Exception("Rental not found");
            }
            if (!existingRental.getUser().getId().equals(reqUser.getId())) {
                throw new Exception("You are not authorized to modify this rental");
            }
            existingRental.setName(rentalDto.getName());
            existingRental.setSurface(rentalDto.getSurface());
            existingRental.setPrice(rentalDto.getPrice());
            existingRental.setDescription(rentalDto.getDescription());
            rentalService.saveRental(existingRental);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Rental updated !"));

        } catch (Exception e) {
            String error = e.getMessage();
            int httpCode = HttpStatus.BAD_REQUEST.value();
            if (error.equals("Rental not found")) {
                httpCode = HttpStatus.NOT_FOUND.value();
            }
            ErrorResponseModel errorResponse = new ErrorResponseModel(httpCode, error);
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        }
    }


    private String savePictureToGetPath(MultipartFile pictureFile) throws Exception {
        // Définir le répertoire de stockage
        String staticDir = System.getProperty("user.dir") + "/";
        Path uploadPath = Paths.get(staticDir);

        // Générer un nom de fichier unique
        String fileName = "uploads/" + System.currentTimeMillis() + "_" + pictureFile.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Enregistrer le fichier
        Files.copy(pictureFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Retourner une URL sous /static/uploads
        return "http://localhost:3001/" + fileName;
    }

}
