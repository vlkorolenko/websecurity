package com.docushield.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "documents")
public class DocumentItem {

    @Id
    private String id;

    @NotBlank(message = "Document title cannot be blank")
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Classification level is required")
    private ClassificationLevel classificationLevel;

    @NotBlank(message = "Department cannot be blank")
    private String department;

    private String owner;
    private String checksumSha256;
    private Long fileSizeBytes;
    private List<String> tags = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public DocumentItem() {}

    public DocumentItem(String id, String title, String description, ClassificationLevel classificationLevel,
                        String department, String owner, String checksumSha256, Long fileSizeBytes,
                        List<String> tags, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.classificationLevel = classificationLevel;
        this.department = department;
        this.owner = owner;
        this.checksumSha256 = checksumSha256;
        this.fileSizeBytes = fileSizeBytes;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ClassificationLevel getClassificationLevel() { return classificationLevel; }
    public void setClassificationLevel(ClassificationLevel classificationLevel) { this.classificationLevel = classificationLevel; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public String getChecksumSha256() { return checksumSha256; }
    public void setChecksumSha256(String checksumSha256) { this.checksumSha256 = checksumSha256; }

    public Long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
