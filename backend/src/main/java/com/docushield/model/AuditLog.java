package com.docushield.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;
    private String username;
    private AuditAction action;
    private String resource;
    private String resourceId;
    private String httpMethod;
    private String clientIp;
    private Integer statusCode;
    private String details;

    @CreatedDate
    private Instant timestamp;

    public AuditLog() {}

    public AuditLog(String id, String username, AuditAction action, String resource, String resourceId,
                    String httpMethod, String clientIp, Integer statusCode, String details, Instant timestamp) {
        this.id = id;
        this.username = username;
        this.action = action;
        this.resource = resource;
        this.resourceId = resourceId;
        this.httpMethod = httpMethod;
        this.clientIp = clientIp;
        this.statusCode = statusCode;
        this.details = details;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public AuditAction getAction() { return action; }
    public void setAction(AuditAction action) { this.action = action; }

    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }

    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }

    public Integer getStatusCode() { return statusCode; }
    public void setStatusCode(Integer statusCode) { this.statusCode = statusCode; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
