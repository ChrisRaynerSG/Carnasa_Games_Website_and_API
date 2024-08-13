package com.sparta.cr.carnasagameswebsiteandapi.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AnonymousAuthentication {

    public static Authentication ensureAuthentication(Authentication authentication) {
        if(authentication == null){
            authentication = SecurityContextHolder.getContext().getAuthentication();
        }
        return authentication;
    }
}
