package oc.chatopbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oc.chatopbackend.configuration.JwtUtils;
import oc.chatopbackend.dto.AuthLoginDto;
import oc.chatopbackend.dto.AuthRegisterDto;
import oc.chatopbackend.entity.UserEntity;
import oc.chatopbackend.model.ErrorResponseModel;
import oc.chatopbackend.model.UserModel;
import oc.chatopbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "AUTHENTICATION", description = "Endpoints for authentication manipulations")
public class AuthController {
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    @Operation(
            summary = "Post a new user",
            description = "Add a new user to the database and return a JWT token"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User added successfully, return a token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"token\":\"jwt\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Email already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                            ErrorResponseModel.class))
            )
    })
    public ResponseEntity<?> register(@RequestBody AuthRegisterDto authRegisterDto) {
        try {
            UserEntity newUser = userService.registerUser(authRegisterDto);
            Map<String, Object> authData = new HashMap<>();
            authData.put("token", jwtUtils.generateToken(newUser.getEmail()));
            return ResponseEntity.ok(authData);
        } catch (Exception e) {
            String message = e.getMessage();
            log.warn(message);
            ErrorResponseModel errorResponse = new ErrorResponseModel(HttpStatus.BAD_REQUEST.value(), message);
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        }
    }

    @PostMapping("/login")
    @Operation(
            summary = "Post a login demand",
            description = "Authenticate a user and return a JWT token"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful, return a token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"token\":\"jwt\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Errors in the authentication process",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                            ErrorResponseModel.class))
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Authentification data",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthLoginDto.class),
                    examples = @ExampleObject(
                            name = "Example",
                            value = "{\"email\":\"test@dev.fr\", \"password\":\"jeSuisUnSuperMotDePasseComplexe!!123\"}"
                    )
            )
    )
    public ResponseEntity<?> login(@RequestBody AuthLoginDto authLoginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authLoginDto.getEmail(),
                            authLoginDto.getPassword()
                    )
            );
            if (authentication.isAuthenticated()) {
                Map<String, Object> authData = new HashMap<>();
                authData.put("token", jwtUtils.generateToken(authLoginDto.getEmail()));
                return ResponseEntity.ok(authData);
            }
            ErrorResponseModel errorResponse = new ErrorResponseModel(HttpStatus.UNAUTHORIZED.value(),
                                                                      "error");
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        } catch (Exception e) {
            String message = e.getMessage();
            log.warn(message);
            ErrorResponseModel errorResponse = new ErrorResponseModel(HttpStatus.UNAUTHORIZED.value(),
                                                                      "error");
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        }
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get the current user",
            description = "Return the current user public informations",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Send the current user",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                            UserModel.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                            ErrorResponseModel.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found in DB but valid token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                            ErrorResponseModel.class))
            )
    })
    public ResponseEntity<?> getTokenFromRequest(HttpServletRequest request) {
        try {
            UserEntity user = (UserEntity) request.getAttribute("user");
            if (user != null) {
                UserModel me = userService.convertToUserModel(user);
                return ResponseEntity.ok(me);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        } catch (Exception e) {
            if ("user not found".equals(e.getMessage())) {
                log.error("An unknown user tried to ask for me info with a valid token !!! ");
                ErrorResponseModel errorResponse = new ErrorResponseModel(HttpStatus.NOT_FOUND.value(), e.getMessage());
                return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
            }
            log.error(e.getMessage());
            ErrorResponseModel errorResponse = new ErrorResponseModel(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        }
    }
}
