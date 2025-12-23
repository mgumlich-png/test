package com.example.gks.service;

import com.example.gks.domain.*;
import com.example.gks.dto.CaseDto;
import com.example.gks.repository.CaseRecordRepository;
import com.example.gks.repository.DocumentRecordRepository;
import com.example.gks.repository.PartnerRepository;
import com.example.gks.repository.TenantRepository;
import com.example.gks.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class CaseService {

    private final CaseRecordRepository caseRecordRepository;
    private final PartnerRepository partnerRepository;
    private final TenantRepository tenantRepository;
    private final UserAccountRepository userAccountRepository;
    private final DocumentRecordRepository documentRecordRepository;
    private final AuditService auditService;

    public CaseService(CaseRecordRepository caseRecordRepository, PartnerRepository partnerRepository, TenantRepository tenantRepository,
                       UserAccountRepository userAccountRepository, DocumentRecordRepository documentRecordRepository, AuditService auditService) {
        this.caseRecordRepository = caseRecordRepository;
        this.partnerRepository = partnerRepository;
        this.tenantRepository = tenantRepository;
        this.userAccountRepository = userAccountRepository;
        this.documentRecordRepository = documentRecordRepository;
        this.auditService = auditService;
    }

    public List<CaseRecord> findVisibleFor(String actor) {
        UserAccount user = getUser(actor);
        if (hasAnyRole(user, Set.of(Role.TENANT_ADMIN, Role.CASE_WORKER))) {
            if (user.getTenant() == null) {
                return caseRecordRepository.findAll();
            }
            return caseRecordRepository.findByTenant(user.getTenant());
        }
        if (hasAnyRole(user, Set.of(Role.PARTNER_ADMIN, Role.PARTNER_USER))) {
            Partner partner = user.getPartner();
            if (partner == null) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Partner context required");
            }
            return caseRecordRepository.findByPartner(partner);
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No role to view cases");
    }

    public CaseRecord create(CaseDto dto, String actor) {
        UserAccount user = getUser(actor);
        Tenant tenant = tenantRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
        Partner partner = dto.getPartnerId() != null ? partnerRepository.findById(dto.getPartnerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Partner not found")) : null;
        validateTenantContext(user, tenant, partner);

        CaseRecord record = new CaseRecord();
        record.setCaseType(dto.getCaseType());
        record.setStatus(CaseStatus.DRAFT);
        record.setTenant(tenant);
        record.setPartner(partner);
        record.setReference(dto.getReference());
        record.setPayloadSummary(dto.getPayloadSummary());
        CaseRecord saved = caseRecordRepository.save(record);
        auditService.record(actor, "CASE_CREATED", "CaseRecord", saved.getId().toString(), saved.getCaseType().name());
        return saved;
    }

    public CaseRecord updateStatus(Long id, CaseStatus status, String actor) {
        CaseRecord record = requireReadableCase(id, actor);
        if (status == CaseStatus.SENT && documentRecordRepository.countByCaseRecord(record) == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Documents required before sending");
        }
        record.setStatus(status);
        CaseRecord saved = caseRecordRepository.save(record);
        auditService.record(actor, "CASE_STATUS", "CaseRecord", saved.getId().toString(), status.name());
        return saved;
    }

    public CaseRecord requireReadableCase(Long caseId, String actor) {
        UserAccount user = getUser(actor);
        CaseRecord record = caseRecordRepository.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));
        if (hasAnyRole(user, Set.of(Role.TENANT_ADMIN, Role.CASE_WORKER))) {
            if (user.getTenant() == null || record.getTenant().equals(user.getTenant())) {
                return record;
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tenant mismatch");
        }
        if (hasAnyRole(user, Set.of(Role.PARTNER_ADMIN, Role.PARTNER_USER))) {
            if (user.getPartner() != null && user.getPartner().equals(record.getPartner())) {
                return record;
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Partner mismatch");
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No access to case");
    }

    private UserAccount getUser(String email) {
        return userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private void validateTenantContext(UserAccount user, Tenant tenant, Partner partner) {
        if (hasAnyRole(user, Set.of(Role.TENANT_ADMIN, Role.CASE_WORKER))) {
            if (user.getTenant() != null && !user.getTenant().equals(tenant)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot create case for another tenant");
            }
        }

        if (hasAnyRole(user, Set.of(Role.PARTNER_ADMIN))) {
            if (user.getPartner() == null || partner == null || !user.getPartner().equals(partner)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Partner admin must target own partner");
            }
            if (!partner.getTenant().equals(tenant)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Partner does not belong to tenant");
            }
        }
    }

    private boolean hasAnyRole(UserAccount user, Set<Role> roles) {
        return user.getRoles().stream().anyMatch(roles::contains);
    }
}
