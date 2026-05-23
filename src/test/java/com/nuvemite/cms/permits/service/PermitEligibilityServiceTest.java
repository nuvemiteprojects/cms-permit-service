package com.nuvemite.cms.permits.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nuvemite.cms.permits.domain.Permit;
import com.nuvemite.cms.permits.domain.PermitEligibilityCache;
import com.nuvemite.cms.permits.domain.PermitType;
import com.nuvemite.cms.permits.exception.UnprocessableEntityException;
import com.nuvemite.cms.permits.repository.PermitEligibilityCacheRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitEligibilityServiceTest {

    @Mock
    private PermitEligibilityCacheRepository cacheRepository;

    @Mock
    private PermitLicenseTypeResolver licenseTypeResolver;

    @InjectMocks
    private PermitEligibilityService eligibilityService;

    @Test
    void requireForSubmit_snapshotsLicenseOnPermit() {
        UUID premiseId = UUID.randomUUID();
        UUID chemicalId = UUID.randomUUID();
        Permit permit = Permit.createDraft(
                "IMP-2026-0001",
                PermitType.IMPORT,
                UUID.randomUUID(),
                premiseId,
                "Acme",
                chemicalId,
                "Acetone",
                BigDecimal.TEN,
                "kg",
                null,
                null,
                null,
                null,
                null,
                null,
                "user");
        when(licenseTypeResolver.requiredLicenseType(PermitType.IMPORT)).thenReturn("Importer");
        PermitEligibilityCache cache = PermitEligibilityCache.fromGrant(
                premiseId,
                chemicalId,
                "Importer",
                UUID.randomUUID(),
                "LIC-001",
                LocalDate.now().minusDays(1),
                LocalDate.now().plusYears(1));
        when(cacheRepository.findByPremiseIdAndChemicalIdAndLicenseType(premiseId, chemicalId, "Importer"))
                .thenReturn(Optional.of(cache));

        eligibilityService.requireForSubmit(permit);

        assertThat(permit.getLicenseId()).isEqualTo(cache.getLicenseId());
        assertThat(permit.getLicenseNumber()).isEqualTo("LIC-001");
    }

    @Test
    void requireForSubmit_throwsWhenNoActiveLicense() {
        Permit permit = Permit.createDraft(
                "IMP-2026-0001",
                PermitType.IMPORT,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Acme",
                UUID.randomUUID(),
                "Acetone",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "user");
        when(licenseTypeResolver.requiredLicenseType(any())).thenReturn("Importer");
        when(cacheRepository.findByPremiseIdAndChemicalIdAndLicenseType(any(), any(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> eligibilityService.requireForSubmit(permit))
                .isInstanceOf(UnprocessableEntityException.class);
    }
}
