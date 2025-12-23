package com.example.gks.service;

import com.example.gks.domain.Partner;
import com.example.gks.domain.Tenant;
import com.example.gks.dto.PartnerDto;
import com.example.gks.repository.PartnerRepository;
import com.example.gks.repository.TenantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final TenantRepository tenantRepository;
    private final AuditService auditService;

    public PartnerService(PartnerRepository partnerRepository, TenantRepository tenantRepository, AuditService auditService) {
        this.partnerRepository = partnerRepository;
        this.tenantRepository = tenantRepository;
        this.auditService = auditService;
    }

    public List<Partner> findAll() {
        return partnerRepository.findAll();
    }

    public Partner create(PartnerDto dto, String actor) {
        Tenant tenant = tenantRepository.findById(dto.getTenantId()).orElseThrow();
        Partner partner = new Partner();
        partner.setName(dto.getName());
        partner.setContactEmail(dto.getContactEmail());
        partner.setTenant(tenant);
        Partner saved = partnerRepository.save(partner);
        auditService.record(actor, "PARTNER_CREATED", "Partner", saved.getId().toString(), saved.getName());
        return saved;
    }

    public Partner update(Long id, PartnerDto dto, String actor) {
        Partner partner = partnerRepository.findById(id).orElseThrow();
        Tenant tenant = tenantRepository.findById(dto.getTenantId()).orElseThrow();
        partner.setName(dto.getName());
        partner.setContactEmail(dto.getContactEmail());
        partner.setTenant(tenant);
        Partner saved = partnerRepository.save(partner);
        auditService.record(actor, "PARTNER_UPDATED", "Partner", saved.getId().toString(), saved.getName());
        return saved;
    }

    public void delete(Long id, String actor) {
        Partner partner = partnerRepository.findById(id).orElseThrow();
        partnerRepository.delete(partner);
        auditService.record(actor, "PARTNER_DELETED", "Partner", id.toString(), partner.getName());
    }
}
