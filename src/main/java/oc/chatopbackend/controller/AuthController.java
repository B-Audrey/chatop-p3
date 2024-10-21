package oc.chatopbackend.controller;

import oc.chatopbackend.dto.AuthLoginDto;
import oc.chatopbackend.dto.AuthRegisterDto;
import oc.chatopbackend.entity.UserEntity;
import oc.chatopbackend.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.webjars.NotFoundException;

@RestController
@RequestMapping("/auth")
@Validated //force la validation des Dto sur chaque route
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<UserEntity> register(@RequestBody AuthRegisterDto authRegisterDto) throws Exception {
        logger.info("je rentre dans la fn register avec, {}", authRegisterDto);
        UserEntity user = userRepository.findByEmail(authRegisterDto.getEmail());
        if (user != null) {
            throw new BadRequestException("Email Already Exists");
        }
        logger.info("j ai pas de user, je peux créer le user");
        UserEntity newUser = new UserEntity();
        newUser.setEmail(authRegisterDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(authRegisterDto.getPassword()));
        newUser.setName(authRegisterDto.getName());
        logger.info("nouvel user {}", newUser);
        userRepository.save(newUser);
        logger.info("save ok je retourne le user");
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthLoginDto authLoginDto) {
        logger.info("je rentre au login");
        UserEntity user = userRepository.findByEmail(authLoginDto.getEmail());
        if (user == null) {
            throw new NotFoundException("Unable to log in");
        }
        if (!passwordEncoder.matches(authLoginDto.getPassword(), user.getPassword())) {
            throw new NotFoundException("Unable to log in");
        }
        logger.info("le password match, je retourne un token");
        String token = "youpi, j ai maintenant la logique jwt à implementer";
        return ResponseEntity.ok("Login successful. Token: " + token);
    }
}
