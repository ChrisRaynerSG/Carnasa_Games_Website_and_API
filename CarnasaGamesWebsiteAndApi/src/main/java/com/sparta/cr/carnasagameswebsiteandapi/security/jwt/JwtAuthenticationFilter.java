package com.sparta.cr.carnasagameswebsiteandapi.security.jwt;

import com.sparta.cr.carnasagameswebsiteandapi.security.SecurityUser;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtilities jwtUtilities;
    private final UserServiceImpl userService;

    public JwtAuthenticationFilter(JwtUtilities jwtUtilities, UserServiceImpl userService) {
        this.jwtUtilities = jwtUtilities;
        this.userService = userService;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt = getJwtFromRequest(request);

        if(jwt != null) {
            String username = jwtUtilities.extractUsername(jwt);
            SecurityUser user = (SecurityUser) userService.loadUserByUsername(username);

            if (jwtUtilities.validateToken(jwt, user)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null; //change to better exception?
    }
}
