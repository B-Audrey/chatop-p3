package oc.chatopbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oc.chatopbackend.model.ErrorResponseModel;
import oc.chatopbackend.model.UserModel;
import oc.chatopbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "USERS", description = "Endpoints for users operations")

public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a user by ID",
            description = "Retrieve a specific user from the database by its unique ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User found and sent in response",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserModel.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseModel.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found in DB",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseModel.class))
            )
    })
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            UserModel user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            String message = e.getMessage();
            log.warn(message);
            ErrorResponseModel errorResponse = new ErrorResponseModel(HttpStatus.NOT_FOUND.value(), message);
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        }
    }
}
