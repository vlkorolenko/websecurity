package com.docushield.service;

import com.docushield.model.ClassificationLevel;
import com.docushield.model.DocumentItem;
import com.docushield.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<DocumentItem> findAll(ClassificationLevel classificationLevel) {
        if (classificationLevel != null) {
            return documentRepository.findByClassificationLevel(classificationLevel);
        }
        return documentRepository.findAll();
    }

    public Optional<DocumentItem> findById(String id) {
        return documentRepository.findById(id);
    }

    public DocumentItem create(DocumentItem item, String currentUser) {
        if (item.getOwner() == null || item.getOwner().isBlank()) {
            item.setOwner(currentUser);
        }
        if (item.getCreatedAt() == null) {
            item.setCreatedAt(Instant.now());
        }
        item.setUpdatedAt(Instant.now());
        return documentRepository.save(item);
    }

    public Optional<DocumentItem> update(String id, DocumentItem updateData) {
        return documentRepository.findById(id).map(existing -> {
            existing.setTitle(updateData.getTitle());
            existing.setDescription(updateData.getDescription());
            existing.setClassificationLevel(updateData.getClassificationLevel());
            existing.setDepartment(updateData.getDepartment());
            if (updateData.getTags() != null) {
                existing.setTags(updateData.getTags());
            }
            if (updateData.getFileSizeBytes() != null) {
                existing.setFileSizeBytes(updateData.getFileSizeBytes());
            }
            if (updateData.getChecksumSha256() != null) {
                existing.setChecksumSha256(updateData.getChecksumSha256());
            }
            existing.setUpdatedAt(Instant.now());
            return documentRepository.save(existing);
        });
    }

    public boolean deleteById(String id) {
        if (documentRepository.existsById(id)) {
            documentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
