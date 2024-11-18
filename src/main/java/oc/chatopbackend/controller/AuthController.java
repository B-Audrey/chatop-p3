package oc.chatopbackend.controller;

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
public class AuthController {

    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRegisterDto authRegisterDto) {
        try {
            UserEntity newUser = userService.registerUser(authRegisterDto);
            UserModel createdUser = userService.convertToUserModel(newUser);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            String message = e.getMessage();
            log.warn(message);
            ErrorResponseModel errorResponse = new ErrorResponseModel(HttpStatus.BAD_REQUEST.value(), message);
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginDto authLoginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authLoginDto.getEmail(),
                    authLoginDto.getPassword()));
            if (authentication.isAuthenticated()) {
                Map<String, Object> authData = new HashMap<>();
                authData.put("token", jwtUtils.generateToken(authLoginDto.getEmail()));
                return ResponseEntity.ok(authData);
            }
            ErrorResponseModel errorResponse = new ErrorResponseModel(HttpStatus.UNAUTHORIZED.value(),
                                                                      "Invalid username or password");
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        } catch (Exception e) {
            String message = e.getMessage();
            log.warn(message);
            ErrorResponseModel errorResponse = new ErrorResponseModel(HttpStatus.UNAUTHORIZED.value(),
                                                                      "Invalid username or password");
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        }
    }

    @GetMapping("/me")
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
                log.warn("An unknown user tried to ask for me info with a valid token!");
                ErrorResponseModel errorResponse = new ErrorResponseModel(HttpStatus.NOT_FOUND.value(), e.getMessage());
                return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
            }
            log.error(e.getMessage());
            ErrorResponseModel errorResponse = new ErrorResponseModel(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
            return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
        }
    }
}
