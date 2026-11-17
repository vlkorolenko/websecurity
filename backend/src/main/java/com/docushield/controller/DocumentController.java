package com.docushield.controller;

import com.docushield.model.AuditAction;
import com.docushield.model.ClassificationLevel;
import com.docushield.model.DocumentItem;
import com.docushield.service.AuditService;
import com.docushield.service.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final AuditService auditService;

    public DocumentController(DocumentService documentService, AuditService auditService) {
        this.documentService = documentService;
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public ResponseEntity<List<DocumentItem>> getAllDocuments(
            @RequestParam(required = false) ClassificationLevel classification,
            HttpServletRequest request) {

        List<DocumentItem> documents = documentService.findAll(classification);
        auditService.logEvent(AuditAction.READ_ALL, "documents", null, "GET",
                request, HttpStatus.OK.value(), "Retrieved " + documents.size() + " documents");

        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public ResponseEntity<DocumentItem> getDocumentById(
            @PathVariable String id,
            HttpServletRequest request) {

        return documentService.findById(id)
                .map(doc -> {
                    auditService.logEvent(AuditAction.READ_ONE, "documents", id, "GET",
                            request, HttpStatus.OK.value(), "Viewed document: " + doc.getTitle());
                    return ResponseEntity.ok(doc);
                })
                .orElseGet(() -> {
                    auditService.logEvent(AuditAction.READ_ONE, "documents", id, "GET",
                            request, HttpStatus.NOT_FOUND.value(), "Document not found: " + id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<DocumentItem> createDocument(
            @Valid @RequestBody DocumentItem documentItem,
            Authentication authentication,
            HttpServletRequest request) {

        String username = authentication != null ? authentication.getName() : "system";
        DocumentItem created = documentService.create(documentItem, username);

        auditService.logEvent(AuditAction.CREATE, "documents", created.getId(), "POST",
                request, HttpStatus.CREATED.value(), "Created document: " + created.getTitle());

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<DocumentItem> updateDocument(
            @PathVariable String id,
            @Valid @RequestBody DocumentItem updateData,
            HttpServletRequest request) {

        return documentService.update(id, updateData)
                .map(updated -> {
                    auditService.logEvent(AuditAction.UPDATE, "documents", id, "PUT",
                            request, HttpStatus.OK.value(), "Updated document: " + updated.getTitle());
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> {
                    auditService.logEvent(AuditAction.UPDATE, "documents", id, "PUT",
                            request, HttpStatus.NOT_FOUND.value(), "Attempted update on missing doc: " + id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDocument(
            @PathVariable String id,
            HttpServletRequest request) {

        boolean deleted = documentService.deleteById(id);
        if (deleted) {
            auditService.logEvent(AuditAction.DELETE, "documents", id, "DELETE",
                    request, HttpStatus.OK.value(), "Deleted document id: " + id);
            return ResponseEntity.ok(java.util.Map.of("message", "Document deleted successfully", "id", id));
        } else {
            auditService.logEvent(AuditAction.DELETE, "documents", id, "DELETE",
                    request, HttpStatus.NOT_FOUND.value(), "Attempted delete on missing doc: " + id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/secret-vault")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getSecretVault(Authentication auth) {
        return ResponseEntity.ok(java.util.Map.of(
                "endpoint", "/secret-vault",
                "annotation", "@PreAuthorize(\"hasRole('ADMIN')\")",
                "status", "Access Granted",
                "user", auth.getName(),
                "data", "Classified Quantum Security Blueprint v4"
        ));
    }

    @GetMapping("/management-overview")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<?> getManagementOverview(Authentication auth) {
        return ResponseEntity.ok(java.util.Map.of(
                "endpoint", "/management-overview",
                "annotation", "@PreAuthorize(\"hasAnyRole('MANAGER', 'ADMIN')\")",
                "status", "Access Granted",
                "user", auth.getName(),
                "data", "Q4 Department Performance & Incident Analytics"
        ));
    }

    @GetMapping("/user-profile")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public ResponseEntity<?> getUserProfile(Authentication auth) {
        return ResponseEntity.ok(java.util.Map.of(
                "endpoint", "/user-profile",
                "annotation", "@PreAuthorize(\"hasAnyRole('USER', 'MANAGER', 'ADMIN')\")",
                "status", "Access Granted",
                "username", auth.getName(),
                "authorities", auth.getAuthorities().toString()
        ));
    }
}
