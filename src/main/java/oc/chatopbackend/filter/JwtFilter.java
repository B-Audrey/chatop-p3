package oc.chatopbackend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oc.chatopbackend.configuration.CustomUserDetailsService;
import oc.chatopbackend.configuration.JwtUtils;
import oc.chatopbackend.entity.UserEntity;
import oc.chatopbackend.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        final String authorizationHeader = req.getHeader("Authorization");
        String useremail = null;
        String jwtToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            useremail = jwtUtils.extractUserEmail(jwtToken);
        }

        if (useremail != null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(useremail);

            if (jwtUtils.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                                                                                                        null,
                                                                                                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                try {
                    UserEntity connectedUser = userService.getUserByEmail(useremail);
                    req.setAttribute("user", connectedUser);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }

        chain.doFilter(req, res);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        List<String> PUBLIC_PATHS = List.of(
                "/api/auth/register",
                "/api/auth/login",
                "/swagger-ui/**"
        );
        String path = request.getServletPath();
        return PUBLIC_PATHS.contains(path);
    }
}
