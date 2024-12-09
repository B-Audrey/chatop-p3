package oc.chatopbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oc.chatopbackend.dto.MessageDto;
import oc.chatopbackend.entity.MessageEntity;
import oc.chatopbackend.entity.RentalEntity;
import oc.chatopbackend.entity.UserEntity;
import oc.chatopbackend.model.ErrorResponseModel;
import oc.chatopbackend.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Valid
@Tag(name = "MESSAGES", description = "Endpoints for messages operations")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @Operation(
            summary = "Post a message to a user",
            description = "Saves the message along with the linked user and rental, and returns a confirmation.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Message sent successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"message\":\"Message sent with success\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                            ErrorResponseModel.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                            ErrorResponseModel.class))
            )
    })
    public ResponseEntity<?> createMessage(@RequestBody MessageDto messageDto, HttpServletRequest request) {
        try {
            UserEntity reqUser = (UserEntity) request.getAttribute("user");

            if (!reqUser.getId().equals(messageDto.getUser_id())) {
                throw new Exception("You are not authorized to send a message as another user");
            }

            RentalEntity rental = messageService.getRentalById(messageDto.getRental_id())
                    .orElseThrow(() -> new Exception("Rental not found"));

            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setMessage(messageDto.getMessage());
            messageEntity.setUser(reqUser);
            messageEntity.setRental(rental);
            messageEntity.setCreatedAt(LocalDateTime.now());

            messageService.saveMessage(messageEntity);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Message sent with success"));

        } catch (Exception e) {
            ErrorResponseModel errorResponse = new ErrorResponseModel(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        }
    }
}
