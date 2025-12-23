package com.example.gks.web;

import com.example.gks.domain.Partner;
import com.example.gks.dto.PartnerDto;
import com.example.gks.service.PartnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partners")
public class PartnerController {

    private final PartnerService partnerService;

    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @GetMapping
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public List<Partner> list() {
        return partnerService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public ResponseEntity<Partner> create(@Validated @RequestBody PartnerDto dto, Authentication authentication) {
        return ResponseEntity.ok(partnerService.create(dto, authentication.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public ResponseEntity<Partner> update(@PathVariable Long id, @Validated @RequestBody PartnerDto dto, Authentication authentication) {
        return ResponseEntity.ok(partnerService.update(id, dto, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        partnerService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
