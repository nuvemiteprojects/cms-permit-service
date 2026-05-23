package com.nuvemite.cms.permits.repository;

import com.nuvemite.cms.permits.domain.PermitEligibilityCache;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermitEligibilityCacheRepository
        extends JpaRepository<PermitEligibilityCache, PermitEligibilityCache.Pk> {

    Optional<PermitEligibilityCache> findByPremiseIdAndChemicalIdAndLicenseType(
            UUID premiseId, UUID chemicalId, String licenseType);

    void deleteByPremiseIdAndChemicalIdAndLicenseType(UUID premiseId, UUID chemicalId, String licenseType);
}
