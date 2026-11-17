package com.docushield.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/csrf")
public class CsrfController {

    @GetMapping
    public Map<String, String> getCsrfToken(CsrfToken csrfToken) {
        if (csrfToken == null) {
            return Map.of("csrf", "disabled", "status", "CSRF is currently disabled or not required");
        }
        return Map.of(
                "parameterName", csrfToken.getParameterName(),
                "headerName", csrfToken.getHeaderName(),
                "token", csrfToken.getToken()
        );
    }
}
