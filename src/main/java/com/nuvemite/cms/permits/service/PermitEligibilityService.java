package com.nuvemite.cms.permits.service;

import com.nuvemite.cms.permits.domain.Permit;
import com.nuvemite.cms.permits.domain.PermitEligibilityCache;
import com.nuvemite.cms.permits.domain.PermitStatus;
import com.nuvemite.cms.permits.exception.UnprocessableEntityException;
import com.nuvemite.cms.permits.messaging.events.LicenseGrantedEvent;
import com.nuvemite.cms.permits.messaging.events.LicenseRevokedEvent;
import com.nuvemite.cms.permits.repository.PermitEligibilityCacheRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermitEligibilityService {

    private final PermitEligibilityCacheRepository cacheRepository;
    private final PermitLicenseTypeResolver licenseTypeResolver;

    public PermitEligibilityService(
            PermitEligibilityCacheRepository cacheRepository, PermitLicenseTypeResolver licenseTypeResolver) {
        this.cacheRepository = cacheRepository;
        this.licenseTypeResolver = licenseTypeResolver;
    }

    public Optional<PermitEligibilityCache> findActive(UUID premiseId, UUID chemicalId, String licenseType) {
        return cacheRepository
                .findByPremiseIdAndChemicalIdAndLicenseType(premiseId, chemicalId, licenseType)
                .filter(c -> c.isActiveOn(LocalDate.now()));
    }

    public Optional<PermitEligibilityCache> findActiveForPermit(Permit permit) {
        String licenseType = licenseTypeResolver.requiredLicenseType(permit.getPermitType());
        return findActive(permit.getApplicantPremiseId(), permit.getChemicalId(), licenseType);
    }

    @Transactional
    public void requireForSubmit(Permit permit) {
        String licenseType = licenseTypeResolver.requiredLicenseType(permit.getPermitType());
        PermitEligibilityCache cache = findActive(permit.getApplicantPremiseId(), permit.getChemicalId(), licenseType)
                .orElseThrow(() -> new UnprocessableEntityException(
                        permit.getPermitType()
                                + " requires applicant with active "
                                + licenseType
                                + " license for "
                                + permit.getChemicalName()
                                + "."));
        permit.snapshotLicense(
                cache.getLicenseId(),
                cache.getLicenseNumber(),
                cache.getLicenseType(),
                cache.getValidFrom(),
                cache.getValidUntil());
    }

    public Optional<PermitEligibilityCache> resolveAssociatedLicense(Permit permit) {
        if (permit.getStatus() != PermitStatus.DRAFT && permit.getLicenseId() != null) {
            return Optional.of(snapshotAsCache(permit));
        }
        return findActiveForPermit(permit);
    }

    private static PermitEligibilityCache snapshotAsCache(Permit permit) {
        return PermitEligibilityCache.fromGrant(
                permit.getApplicantPremiseId(),
                permit.getChemicalId(),
                permit.getLicenseType(),
                permit.getLicenseId(),
                permit.getLicenseNumber(),
                permit.getLicenseValidFrom(),
                permit.getLicenseValidUntil());
    }

    @Transactional
    public void handleGranted(LicenseGrantedEvent event) {
        PermitEligibilityCache cache = cacheRepository
                .findByPremiseIdAndChemicalIdAndLicenseType(
                        event.premiseId(), event.chemicalId(), event.licenseType())
                .orElseGet(() -> PermitEligibilityCache.fromGrant(
                        event.premiseId(),
                        event.chemicalId(),
                        event.licenseType(),
                        event.licenseId(),
                        event.licenseNumber(),
                        event.validFrom(),
                        event.validUntil()));
        cache.applyGrant(
                event.licenseId(),
                event.licenseNumber(),
                event.validFrom(),
                event.validUntil());
        cacheRepository.save(cache);
    }

    @Transactional
    public void handleRevoked(LicenseRevokedEvent event) {
        cacheRepository.deleteByPremiseIdAndChemicalIdAndLicenseType(
                event.premiseId(), event.chemicalId(), event.licenseType());
    }
}
