package com.docushield.config;

import com.docushield.model.AuditAction;
import com.docushield.model.AuditLog;
import com.docushield.model.ClassificationLevel;
import com.docushield.model.DocumentItem;
import com.docushield.repository.AuditLogRepository;
import com.docushield.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final DocumentRepository documentRepository;
    private final AuditLogRepository auditLogRepository;

    public DataLoader(DocumentRepository documentRepository, AuditLogRepository auditLogRepository) {
        this.documentRepository = documentRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void run(String... args) {
        try {
            if (documentRepository.count() == 0) {
                log.info("Seeding initial documents into MongoDB...");

                DocumentItem doc1 = new DocumentItem(
                        null,
                        "Corporate Security Policy 2026",
                        "General cybersecurity protocols and access rules.",
                        ClassificationLevel.INTERNAL,
                        "Cybersecurity",
                        "admin",
                        "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                        1048576L,
                        List.of("policy", "compliance", "security"),
                        Instant.now(),
                        Instant.now()
                );

                DocumentItem doc2 = new DocumentItem(
                        null,
                        "Q3 Financial & Tax Audit Report",
                        "Restricted report on quarterly corporate balance and tax filings.",
                        ClassificationLevel.CONFIDENTIAL,
                        "Finance",
                        "user",
                        "ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb",
                        4502300L,
                        List.of("finance", "audit", "confidential"),
                        Instant.now(),
                        Instant.now()
                );

                DocumentItem doc3 = new DocumentItem(
                        null,
                        "Quantum Key Exchange Blueprint",
                        "Classified research for zero trust network protocol.",
                        ClassificationLevel.SECRET,
                        "R&D Cryptography",
                        "admin",
                        "4b227777d4dd1fc61c6f884f48641d02b4d121d3fd328cb08b5531fcacdabf8a",
                        15234000L,
                        List.of("crypto", "quantum", "top-secret"),
                        Instant.now(),
                        Instant.now()
                );

                documentRepository.saveAll(List.of(doc1, doc2, doc3));
                log.info("Initial documents seeded successfully.");
            }

            if (auditLogRepository.count() == 0) {
                AuditLog initialLog = new AuditLog(
                        null,
                        "system",
                        AuditAction.LOGIN_SUCCESS,
                        "system",
                        "init",
                        "SYSTEM",
                        "127.0.0.1",
                        200,
                        "DocuShield audit sub-system initialized successfully",
                        Instant.now()
                );
                auditLogRepository.save(initialLog);
            }
        } catch (Exception ex) {
            log.warn("Mongo seeding deferred: {}", ex.getMessage());
        }
    }
}
