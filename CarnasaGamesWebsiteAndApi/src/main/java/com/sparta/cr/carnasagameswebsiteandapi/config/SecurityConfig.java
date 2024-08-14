package com.sparta.cr.carnasagameswebsiteandapi.config;

import com.sparta.cr.carnasagameswebsiteandapi.security.jwt.JwtAuthenticationFilter;
import com.sparta.cr.carnasagameswebsiteandapi.security.jwt.JwtUtilities;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecretKey secretKey;
    private final UserServiceImpl userService;
    private final JwtUtilities jwtUtilities;

    @Autowired
    public SecurityConfig(UserServiceImpl userService, JwtUtilities jwtUtilities) {
        this.userService = userService;
        this.jwtUtilities = jwtUtilities;
        this.secretKey = jwtUtilities.getSecret();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtilities,userService), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                        .requestMatchers("/api/carnasa-game-api/v1/scores/update/**"
                                , "/api/carnasa-game-api/v1/scores/delete/**",
                                "/api/carnasa-game-api/v1/users/update/{userId}/roles").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .anonymous(anon -> anon
                        .principal("anonymousUser")
                        .authorities("ROLE_ANONYMOUS"))
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/google")
                        .defaultSuccessUrl("/home")
                        .userInfoEndpoint(userInfo -> userInfo.userService(userService)))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID"))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt-> {
                            jwt.decoder(jwtDecoder());
                            jwt.jwtAuthenticationConverter(jwtAuthenticationConverter());
                        }))
                .build();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                        org.springframework.security.core.Authentication authentication)
                    throws IOException {
                response.sendRedirect("/home");
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt-> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            Collection<String> roles = jwt.getClaimAsStringList("roles");
            if(roles != null) {
                authorities.addAll(roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList());
            }
            return authorities;
        });
        return converter;
    }

}
