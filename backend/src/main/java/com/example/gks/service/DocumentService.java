package com.example.gks.service;

import com.example.gks.domain.CaseRecord;
import com.example.gks.domain.DocumentRecord;
import com.example.gks.dto.DocumentUploadResponse;
import com.example.gks.repository.DocumentRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRecordRepository documentRecordRepository;
    private final FileStorageService fileStorageService;
    private final AuditService auditService;
    private final CaseService caseService;

    public DocumentService(DocumentRecordRepository documentRecordRepository, FileStorageService fileStorageService, AuditService auditService, CaseService caseService) {
        this.documentRecordRepository = documentRecordRepository;
        this.fileStorageService = fileStorageService;
        this.auditService = auditService;
        this.caseService = caseService;
    }

    public List<DocumentRecord> listByCase(Long caseId, String actor) {
        CaseRecord record = caseService.requireReadableCase(caseId, actor);
        return documentRecordRepository.findByCaseRecordOrderByVersionDesc(record);
    }

    public DocumentUploadResponse upload(Long caseId, MultipartFile file, String actor) throws IOException {
        CaseRecord record = caseService.requireReadableCase(caseId, actor);
        List<DocumentRecord> current = documentRecordRepository.findByCaseRecordOrderByVersionDesc(record);
        int version = current.isEmpty() ? 1 : current.get(0).getVersion() + 1;
        String storageKey = fileStorageService.store(file, "case-" + caseId);
        String hash = fileStorageService.hashFile(Path.of(storageKey));

        DocumentRecord doc = new DocumentRecord();
        doc.setCaseRecord(record);
        doc.setFileName(file.getOriginalFilename());
        doc.setStorageKey(storageKey);
        doc.setHash(hash);
        doc.setVersion(version);
        DocumentRecord saved = documentRecordRepository.save(doc);
        auditService.record(actor, "DOCUMENT_UPLOADED", "DocumentRecord", saved.getId().toString(), file.getOriginalFilename());
        return new DocumentUploadResponse(saved.getId(), saved.getVersion(), saved.getFileName(), saved.getStorageKey());
    }
}
