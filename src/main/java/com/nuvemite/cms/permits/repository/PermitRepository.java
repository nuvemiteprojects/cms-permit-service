package com.nuvemite.cms.permits.repository;

import com.nuvemite.cms.permits.domain.Permit;
import com.nuvemite.cms.permits.domain.PermitStatus;
import com.nuvemite.cms.permits.domain.PermitType;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PermitRepository extends JpaRepository<Permit, UUID> {

    long count();

    @Query("""
            SELECT p FROM Permit p
            WHERE (:companyId IS NULL OR p.applicantCompanyId = :companyId)
              AND (:premiseId IS NULL OR p.applicantPremiseId = :premiseId)
              AND (:status IS NULL OR p.status = :status)
              AND (:permitType IS NULL OR p.permitType = :permitType)
            ORDER BY p.createdAt DESC
            """)
    Page<Permit> search(
            UUID companyId, UUID premiseId, PermitStatus status, PermitType permitType, Pageable pageable);
}
