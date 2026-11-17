package com.docushield.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public")
public class PublicController {

    @GetMapping("/info")
    public Map<String, Object> getPublicInfo() {
        return Map.of(
                "application", "DocuShield Web Security System",
                "version", "1.0.0",
                "securityStandard", "BasicAuth + CSRF + Audit Logging",
                "serverTime", Instant.now().toString(),
                "status", "UP"
        );
    }
}
