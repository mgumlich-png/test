package com.example.gks.web;

import com.example.gks.domain.CaseRecord;
import com.example.gks.domain.CaseStatus;
import com.example.gks.dto.CaseDto;
import com.example.gks.service.CaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','CASE_WORKER','PARTNER_ADMIN','PARTNER_USER')")
    public List<CaseRecord> list(Authentication authentication) {
        return caseService.findVisibleFor(authentication.getName());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','CASE_WORKER')")
    public ResponseEntity<CaseRecord> create(@Validated @RequestBody CaseDto dto, Authentication authentication) {
        return ResponseEntity.ok(caseService.create(dto, authentication.getName()));
    }

    @PostMapping("/{id}/status/{status}")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','CASE_WORKER')")
    public ResponseEntity<CaseRecord> updateStatus(@PathVariable Long id, @PathVariable CaseStatus status, Authentication authentication) {
        return ResponseEntity.ok(caseService.updateStatus(id, status, authentication.getName()));
    }
}
