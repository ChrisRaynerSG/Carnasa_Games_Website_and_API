package com.sparta.cr.carnasagameswebsiteandapi.controllers.api;

import com.sparta.cr.carnasagameswebsiteandapi.models.authentication.JwtRequest;
import com.sparta.cr.carnasagameswebsiteandapi.models.authentication.JwtResponse;
import com.sparta.cr.carnasagameswebsiteandapi.security.jwt.JwtUtilities;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.AuthServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.UserServiceImpl;
import org.springframework.web.bind.annotation.*;
import com.sparta.cr.carnasagameswebsiteandapi.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/carnasa-game-api/v1/users/login")
public class AuthenticationApiController {

    private final UserServiceImpl userService;
    private final AuthServiceImpl authService;
    private final JwtUtilities jwtUtilities;

    @Autowired
    public AuthenticationApiController(UserServiceImpl userService, JwtUtilities jwtUtilities, AuthServiceImpl authService) {
        this.userService = userService;
        this.authService = authService;
        this.jwtUtilities = jwtUtilities;
    }

    @PostMapping
    public JwtResponse createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        authService.authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        String token = jwtUtilities.generateToken((SecurityUser) userService.loadUserByUsername(authenticationRequest.getUsername()));
        return new JwtResponse(token);
    }
}
