package com.docushield.service;

import com.docushield.model.AuditAction;
import com.docushield.model.AuditLog;
import com.docushield.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLog logEvent(AuditAction action, String resource, String resourceId, String httpMethod,
                             HttpServletRequest request, Integer statusCode, String details) {
        String username = getCurrentUsername();
        String clientIp = resolveClientIp(request);

        AuditLog entry = new AuditLog(
                null,
                username,
                action,
                resource,
                resourceId,
                httpMethod,
                clientIp,
                statusCode,
                details,
                Instant.now()
        );

        try {
            AuditLog saved = auditLogRepository.save(entry);
            log.info("AUDIT EVENT [{}]: user={}, resource={}, id={}, status={}",
                    action, username, resource, resourceId, statusCode);
            return saved;
        } catch (Exception ex) {
            log.warn("Failed to persist audit log into MongoDB: {}", ex.getMessage());
            return entry;
        }
    }

    public List<AuditLog> getRecentLogs() {
        try {
            return auditLogRepository.findTop50ByOrderByTimestampDesc();
        } catch (Exception ex) {
            log.warn("Could not query audit logs from MongoDB: {}", ex.getMessage());
            return List.of();
        }
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "anonymous";
        }
        return auth.getName();
    }

    private String resolveClientIp(HttpServletRequest request) {
        if (request == null) {
            return "127.0.0.1";
        }
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || "unknown".equalsIgnoreCase(xfHeader)) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
