package com.example.gks.web;

import com.example.gks.domain.DocumentRecord;
import com.example.gks.dto.DocumentUploadResponse;
import com.example.gks.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/case/{caseId}")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','CASE_WORKER','PARTNER_ADMIN','PARTNER_USER')")
    public List<DocumentRecord> list(@PathVariable Long caseId, Authentication authentication) {
        return documentService.listByCase(caseId, authentication.getName());
    }

    @PostMapping("/case/{caseId}")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','CASE_WORKER','PARTNER_ADMIN')")
    public ResponseEntity<DocumentUploadResponse> upload(@PathVariable Long caseId, @RequestParam("file") MultipartFile file, Authentication authentication) throws IOException {
        return ResponseEntity.ok(documentService.upload(caseId, file, authentication.getName()));
    }
}
