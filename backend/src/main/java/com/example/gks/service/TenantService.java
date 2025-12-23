package com.example.gks.service;

import com.example.gks.domain.Tenant;
import com.example.gks.dto.TenantDto;
import com.example.gks.repository.TenantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;
    private final AuditService auditService;

    public TenantService(TenantRepository tenantRepository, AuditService auditService) {
        this.tenantRepository = tenantRepository;
        this.auditService = auditService;
    }

    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }

    public Tenant create(TenantDto dto, String actor) {
        Tenant tenant = new Tenant();
        tenant.setName(dto.getName());
        tenant.setContactEmail(dto.getContactEmail());
        Tenant saved = tenantRepository.save(tenant);
        auditService.record(actor, "TENANT_CREATED", "Tenant", saved.getId().toString(), saved.getName());
        return saved;
    }

    public Tenant update(Long id, TenantDto dto, String actor) {
        Tenant tenant = tenantRepository.findById(id).orElseThrow();
        tenant.setName(dto.getName());
        tenant.setContactEmail(dto.getContactEmail());
        Tenant saved = tenantRepository.save(tenant);
        auditService.record(actor, "TENANT_UPDATED", "Tenant", saved.getId().toString(), saved.getName());
        return saved;
    }

    public void delete(Long id, String actor) {
        Tenant tenant = tenantRepository.findById(id).orElseThrow();
        tenantRepository.delete(tenant);
        auditService.record(actor, "TENANT_DELETED", "Tenant", id.toString(), tenant.getName());
    }
}
