package oc.chatopbackend.service;

import oc.chatopbackend.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;


@Service
public class JwtService {

    public static final Logger logger = LoggerFactory.getLogger(JwtService.class);


    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public String generateToken(UserEntity user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.DAYS))
                .id(String.valueOf(user.getId()))
                .subject(String.valueOf(user.getName()))
                .build();
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256)
                                                                                      .build(), claims);
        return this.jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
    }

    public Map<String, Object> decryptUserByItsToken(String token) {
        try {
            Jwt decodedToken = this.jwtDecoder.decode(token);
            Instant now = Instant.now();
            Map<String, Object> claims = decodedToken.getClaims();
            String tokenExpirationDate = claims.get("exp").toString();
            Instant expirationInstant = Instant.parse(tokenExpirationDate);
            var isTokenValid = now.isBefore(expirationInstant);
            if (!isTokenValid) {
                throw new JwtException("Invalid token");
            }
            return claims;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }
}

