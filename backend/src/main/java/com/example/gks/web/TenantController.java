package com.example.gks.web;

import com.example.gks.domain.Tenant;
import com.example.gks.dto.TenantDto;
import com.example.gks.service.TenantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public List<Tenant> list() {
        return tenantService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public ResponseEntity<Tenant> create(@Validated @RequestBody TenantDto dto, Authentication authentication) {
        return ResponseEntity.ok(tenantService.create(dto, authentication.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public ResponseEntity<Tenant> update(@PathVariable Long id, @Validated @RequestBody TenantDto dto, Authentication authentication) {
        return ResponseEntity.ok(tenantService.update(id, dto, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        tenantService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
