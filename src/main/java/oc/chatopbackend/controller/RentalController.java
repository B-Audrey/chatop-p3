package oc.chatopbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oc.chatopbackend.dto.RentalDto;
import oc.chatopbackend.entity.RentalEntity;
import oc.chatopbackend.entity.UserEntity;
import oc.chatopbackend.model.ErrorResponseModel;
import oc.chatopbackend.service.RentalService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createRental(HttpServletRequest request, @ModelAttribute RentalDto rentalDto) {
        try {
            UserEntity reqUser = (UserEntity) request.getAttribute("user");

            MultipartFile pictureFile = rentalDto.getPicture();

            if (pictureFile != null && !pictureFile.isEmpty()) {
                RentalEntity rentalEntity = convertToEntity(rentalDto);
                String picturePath = savePictureToGetPath(pictureFile);
                rentalEntity.setPicture(picturePath);
                rentalEntity.setOwnerId(reqUser.getId());
                RentalEntity rentalEntitySaved = rentalService.createRental(rentalEntity, reqUser.getId());
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
            ErrorResponseModel errorResponse = new ErrorResponseModel(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        }
    }

    // Méthode pour sauvegarder le fichier image sur le serveur
    private String savePictureToGetPath(MultipartFile pictureFile) throws Exception {
        // Récupérer le répertoire de base de l'application
        String projectDir = System.getProperty("user.dir");
        String uploadDir = projectDir + "/uploads/";

        // Créer le dossier "uploads" s'il n'existe pas
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = System.currentTimeMillis() + "_" + pictureFile.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.copy(pictureFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filePath.toString();
    }

}
