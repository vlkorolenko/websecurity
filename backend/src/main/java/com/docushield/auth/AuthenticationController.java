package com.docushield.auth;

import com.docushield.jwt.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final JwtService jwtService;

    public AuthenticationController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody AuthenticationRequest request) {
        String email = request.getEmail() != null ? request.getEmail() : "john@mail.com";
        String fullName;
        List<String> authorities;

        if (email.contains("admin")) {
            fullName = "Security Admin";
            authorities = List.of("ADMIN", "MANAGER", "USER");
        } else if (email.contains("manager")) {
            fullName = "Security Manager";
            authorities = List.of("MANAGER");
        } else {
            fullName = "John Lennon";
            authorities = List.of("USER");
        }

        String jwtToken = jwtService.generateTokenDirect(email, fullName, authorities);
        return ResponseEntity.ok(jwtToken);
    }
}
