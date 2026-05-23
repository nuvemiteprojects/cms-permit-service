package com.nuvemite.cms.permits.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.nuvemite.cms.permits.domain.PermitType;
import org.junit.jupiter.api.Test;

class PermitLicenseTypeResolverTest {

    private final PermitLicenseTypeResolver resolver = new PermitLicenseTypeResolver();

    @Test
    void mapsAllPermitTypes() {
        assertThat(resolver.requiredLicenseType(PermitType.IMPORT)).isEqualTo("Importer");
        assertThat(resolver.requiredLicenseType(PermitType.EXPORT)).isEqualTo("Exporter");
        assertThat(resolver.requiredLicenseType(PermitType.MANUFACTURING)).isEqualTo("Manufacturer");
        assertThat(resolver.requiredLicenseType(PermitType.STORAGE)).isEqualTo("Storage Operator");
        assertThat(resolver.requiredLicenseType(PermitType.TRANSPORT)).isEqualTo("Transporter");
        assertThat(resolver.requiredLicenseType(PermitType.USE)).isEqualTo("Facility Operator");
        assertThat(resolver.requiredLicenseType(PermitType.WASTE_HANDLING)).isEqualTo("Waste Handler");
        assertThat(resolver.requiredLicenseType(PermitType.DISPOSAL)).isEqualTo("Disposal Facility");
    }

    @Test
    void normalizesPermitTypeStrings() {
        assertThat(PermitLicenseTypeResolver.normalizePermitType("Import Permit")).isEqualTo("IMPORT_PERMIT");
    }
}
