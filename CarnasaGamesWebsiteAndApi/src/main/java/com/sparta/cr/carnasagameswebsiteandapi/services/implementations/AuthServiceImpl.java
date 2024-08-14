package com.sparta.cr.carnasagameswebsiteandapi.services.implementations;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.PasswordMatchException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.InvalidUserException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.InvalidPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl {

    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager, UserServiceImpl userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    public void authenticate(String username, String password) throws Exception {
        try {
            userService.loadUserByUsername(username);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        }
        catch (UsernameNotFoundException e) {
            throw new ModelNotFoundException("Username: "+ username + " not found");
        }
        catch (BadCredentialsException e) {
            if(!userService.validatePassword(password)){
                throw new InvalidPasswordException();
            }
            throw new PasswordMatchException("Incorrect password provided for user: " + username); //401 exception
        }
        catch (Exception e) {
            throw new InvalidUserException("Invalid credentials provided");
        }
    }
}
