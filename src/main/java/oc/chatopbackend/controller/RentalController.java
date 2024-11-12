package oc.chatopbackend.controller;

import oc.chatopbackend.dto.RentalDto;
import oc.chatopbackend.entity.RentalEntity;
import oc.chatopbackend.model.ErrorResponseModel;
import oc.chatopbackend.service.RentalService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
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
import java.util.UUID;

@RestController
@RequestMapping("/rentals")
@Validated
public class RentalController {
    public static final Logger logger = LoggerFactory.getLogger(RentalController.class);

    private final RentalService rentalService;
    private final ModelMapper modelMapper;

    @Autowired
    public RentalController(RentalService rentalService, ModelMapper modelMapper) {
        this.rentalService = rentalService;
        this.modelMapper = modelMapper;
    }

    public RentalEntity convertToEntity(RentalDto rentalDto) {
        return modelMapper.map(rentalDto, RentalEntity.class);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createRental(@ModelAttribute RentalDto rentalDto, Authentication authentication) {
        try {
            // Récupérer l'ID du user depuis l'authentification
            Jwt jwt = (Jwt) authentication.getPrincipal(); // Cast du principal en Jwt
            String userId = jwt.getClaim("jti");

            logger.info("j ai un user avec un id {}", userId);

            // Extraire l'image
            MultipartFile pictureFile = rentalDto.getPicture();
            logger.info("j ai une image");

            // si j'ai bien une image extraite je continue
            if (pictureFile != null && !pictureFile.isEmpty()) {
                logger.info("mon image est valide");
                //transformer en entity
                RentalEntity rentalEntity = convertToEntity(rentalDto);
                // si tout s'est bien passé, save l'image et accrocher le path a l'entity
                String picturePath = savePicture(pictureFile);
                logger.info("j ai bien enregistre l image");
                rentalEntity.setPicture(picturePath);
                // Appeler le service pour créer la location en base avec le chemin ou l'img a ete saved
                rentalService.createRental(rentalEntity, Integer.parseInt(userId));
                logger.info("j ai bien sauve le rental");
            }

            return ResponseEntity.ok("Rental created !");
        } catch (Exception e) {
            ErrorResponseModel errorResponse = new ErrorResponseModel(HttpStatus.NOT_FOUND.value(),
                                                                      e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(errorResponse);
        }
    }

    // Méthode pour sauvegarder le fichier image sur le serveur
    private String savePicture(MultipartFile pictureFile) throws Exception {
        // Récupérer le répertoire de base de l'application
        String projectDir = System.getProperty("user.dir");
        logger.info("j ai un dir, {} ", projectDir);
        String uploadDir = projectDir + "/uploads/";
        logger.info("j ai un upload dir, {} ", uploadDir);
        // Créer le dossier "uploads" s'il n'existe pas
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            logger.info("je dois créer pour la premier fois le dir");
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID() + "_" + pictureFile.getOriginalFilename();
        logger.info("j ai un fileName: {} ", fileName);
        Path filePath = Paths.get(uploadDir + fileName);
        logger.info("j ai un path: {}", filePath);
        Files.copy(pictureFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        logger.info("j ai save l img dans le dossier uploads ");
        return filePath.toString();
    }

}
