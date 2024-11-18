package oc.chatopbackend.service;

import lombok.RequiredArgsConstructor;
import oc.chatopbackend.dto.AuthRegisterDto;
import oc.chatopbackend.entity.UserEntity;
import oc.chatopbackend.model.UserModel;
import oc.chatopbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserModel convertToUserModel(UserEntity user) {
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

    public UserModel getUserById(Long id) throws Exception {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new Exception("user not found"));
        return convertToUserModel(userEntity);
    }
}
