package oc.chatopbackend.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SpringSecurityConfig.class);
    private final CustomUserDetailsService customUserDetailsServiceInstance;

    public SpringSecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsServiceInstance = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("initialisation du filtre spring security");

        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth                          // lambda fn pour conf l'auth
                .requestMatchers("/auth/**").permitAll()     // Routes ne necessitant pas d'auth commencent pas /api/auth/tout
                .anyRequest().authenticated()                            // Toute autre requête nécessite une authentification
        );
        return http.build();                                                // build le tout
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();                               // instancie et mets l'encodeur a dispo
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder bCryptPasswordEncoder) throws Exception {
        logger.info("je suis dans le auth manager pour initialisation");
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsServiceInstance)           // passe notre service à celui attendu par la class de Spring
                .passwordEncoder(bCryptPasswordEncoder);                      // passe l'encodeur de mot de passe
        return authenticationManagerBuilder.build();                          // retourne le build du tout
    }


}
