package oc.chatopbackend.controller;

import oc.chatopbackend.dto.AuthLoginDto;
import oc.chatopbackend.dto.AuthRegisterDto;
import oc.chatopbackend.entity.UserEntity;
import oc.chatopbackend.model.UserModel;
import oc.chatopbackend.service.JwtService;
import oc.chatopbackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    public static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public AuthController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody AuthRegisterDto authRegisterDto) {
        try {
            UserEntity newUser = userService.registerUser(authRegisterDto);
            UserModel createdUser = userService.convertToDto(newUser);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            String message = e.getMessage();
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(message);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthLoginDto authLoginDto) {
        try {
            UserEntity user = userService.getUserByEmail(authLoginDto.getEmail());
            if (!userService.validatePassword(authLoginDto.getPassword(), user.getPassword())) {
                throw new NotFoundException("Unable to log in");
            }
            String token = jwtService.generateToken(user);
            Map<String, String> response = Map.of("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String message = e.getMessage();
            logger.error(message);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", message));
        }
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity getTokenFromRequest(@RequestHeader("Authorization") String authorizationHeader) throws Exception {
        try {
            String token = authorizationHeader.substring(7);
            Map<String, Object> decodedJwtData = jwtService.decryptUserByItsToken(token);
            String userIdStr = (String) decodedJwtData.get("jti");
            int userId = Integer.parseInt(userIdStr);
            UserModel user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (Error e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
