package com.docushield.repository;

import com.docushield.model.ClassificationLevel;
import com.docushield.model.DocumentItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends MongoRepository<DocumentItem, String> {
    List<DocumentItem> findByClassificationLevel(ClassificationLevel classificationLevel);
    List<DocumentItem> findByDepartment(String department);
    List<DocumentItem> findByOwner(String owner);
}
