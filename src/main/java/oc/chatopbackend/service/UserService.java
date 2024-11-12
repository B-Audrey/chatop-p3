package oc.chatopbackend.service;

import oc.chatopbackend.dto.AuthRegisterDto;
import oc.chatopbackend.entity.UserEntity;
import oc.chatopbackend.model.UserModel;
import oc.chatopbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserModel convertToDto(UserEntity user) {
        return new UserModel(user.getId(),
                             user.getEmail(),
                             user.getName(),
                             user.getCreated_at(),
                             user.getUpdated_at());
    }

    public UserEntity registerUser(AuthRegisterDto authRegisterDto) throws Exception {
        // Vérifier si l'email existe déjà
        if (userRepository.findByEmail(authRegisterDto.getEmail()) != null) {
            throw new Exception("Email Already Exists");
        }
        // Créer et enregistrer le nouvel utilisateur
        UserEntity newUser = new UserEntity();
        newUser.setEmail(authRegisterDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(authRegisterDto.getPassword()));
        newUser.setName(authRegisterDto.getName());
        return userRepository.save(newUser);
    }

    public UserEntity getUserByEmail(String email) throws Exception {
        return Optional.ofNullable(userRepository.findByEmail(email))
                .orElseThrow(() -> new Exception("User not found"));
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public UserModel getUserById(int id) throws Exception {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new Exception("user not found"));
        return convertToDto(userEntity);
    }
}
