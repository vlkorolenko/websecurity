package com.docushield;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class DocuShieldApplication {
    public static void main(String[] args) {
        SpringApplication.run(DocuShieldApplication.class, args);
    }
}
