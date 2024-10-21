package oc.chatopbackend.configuration;

import oc.chatopbackend.entity.UserEntity;
import oc.chatopbackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepositoryInstance;                   // le repo du service

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepositoryInstance = userRepository;                         // associe mon repo instancié à celui du service
    }

    @Override
    // méthode attendue pas Spring securité, override par la mienne
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("je rentre dans la validation de spring security pour chercher le user");
        UserEntity user = userRepositoryInstance.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        logger.info("j ai un user, je le retourne");
        return user;
    }
}
