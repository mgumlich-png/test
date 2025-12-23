package com.example.gks.repository;

import com.example.gks.domain.CaseRecord;
import com.example.gks.domain.DocumentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRecordRepository extends JpaRepository<DocumentRecord, Long> {
    List<DocumentRecord> findByCaseRecordOrderByVersionDesc(CaseRecord caseRecord);
    long countByCaseRecord(CaseRecord caseRecord);
}
