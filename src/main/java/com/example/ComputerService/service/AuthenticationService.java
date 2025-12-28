package com.example.ComputerService.service;

import com.example.ComputerService.dto.request.LoginRequest;
import com.example.ComputerService.dto.response.AuthResponse;
import com.example.ComputerService.security.CustomUserDetailsService;
import com.example.ComputerService.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        UserDetails user = userDetailsService.loadUserByUsername(request.getEmail());

        String jwtToken = jwtService.generateToken(user);

        String role = user.getAuthorities().iterator().next().getAuthority();

        return AuthResponse.builder()
                .token(jwtToken)
                .role(role)
                .username(user.getUsername())
                .build();
    }
}
