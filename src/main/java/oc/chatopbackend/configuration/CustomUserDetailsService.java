package oc.chatopbackend.configuration;

import oc.chatopbackend.entity.UserEntity;
import oc.chatopbackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepositoryInstance;                   // le repo du service

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepositoryInstance = userRepository;         // associe mon repo instancié à celui du service
    }

    @Override
    // méthode attendue par Spring security, override par la mienne
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepositoryInstance.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
}
