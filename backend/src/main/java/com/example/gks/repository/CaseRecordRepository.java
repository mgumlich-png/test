package com.example.gks.repository;

import com.example.gks.domain.CaseRecord;
import com.example.gks.domain.Partner;
import com.example.gks.domain.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseRecordRepository extends JpaRepository<CaseRecord, Long> {
    List<CaseRecord> findByTenant(Tenant tenant);
    List<CaseRecord> findByPartner(Partner partner);
}
