package oc.chatopbackend.controller;

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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rentals")
@Slf4j
@RequiredArgsConstructor
@Validated
public class RentalController {

    private final RentalService rentalService;
    private final ModelMapper modelMapper;

    public RentalEntity convertToEntity(RentalDto rentalDto) {
        return modelMapper.map(rentalDto, RentalEntity.class);
    }

    public RentalModel convertToModel(RentalEntity rentalEntity) {
        return modelMapper.map(rentalEntity, RentalModel.class);
    }

    @GetMapping
    public ResponseEntity<?> getAllRentals() {
        try {
            List<RentalEntity> rentalEntities = rentalService.getAllRentals();

            List<RentalModel> rentals = rentalEntities.stream()
                    .map(this::convertToModel)
                    .toList();
            return ResponseEntity.ok(Map.of("rentals", rentals));

        } catch (Exception e) {
            ErrorResponseModel errorResponse = new ErrorResponseModel(HttpStatus.NOT_FOUND.value(), e.getMessage());
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
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
                    return ResponseEntity.ok("Rental created !");
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
    public ResponseEntity<?> updateRental(HttpServletRequest request, @ModelAttribute RentalUpdateDto rentalDto,
            @PathVariable Long rentalId) {
        try {
            UserEntity reqUser = (UserEntity) request.getAttribute("user");
            log.debug("{} is updating its rental {}", reqUser.toString(), rentalId);
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
            return ResponseEntity.ok("Rental updated !");

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
        String projectDir = System.getProperty("user.dir");
        String uploadDir = projectDir + "/src/main/uploads/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = System.currentTimeMillis() + "_" + pictureFile.getOriginalFilename(); // use time to avoid
        // doubles
        Path filePath = Paths.get(uploadDir + fileName);
        return filePath.toString();
    }


}
