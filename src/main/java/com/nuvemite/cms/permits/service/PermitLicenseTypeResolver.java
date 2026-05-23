package com.nuvemite.cms.permits.service;

import com.nuvemite.cms.permits.domain.PermitType;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class PermitLicenseTypeResolver {

    private static final Map<PermitType, String> PERMIT_TO_LICENSE = Map.of(
            PermitType.IMPORT, "Importer",
            PermitType.EXPORT, "Exporter",
            PermitType.MANUFACTURING, "Manufacturer",
            PermitType.STORAGE, "Storage Operator",
            PermitType.TRANSPORT, "Transporter",
            PermitType.USE, "Facility Operator",
            PermitType.WASTE_HANDLING, "Waste Handler",
            PermitType.DISPOSAL, "Disposal Facility");

    public String requiredLicenseType(PermitType permitType) {
        return Optional.ofNullable(PERMIT_TO_LICENSE.get(permitType))
                .orElseThrow(() -> new IllegalArgumentException("Unknown permit type: " + permitType));
    }

    public static String normalizePermitType(String permitType) {
        if (permitType == null) {
            return "";
        }
        return permitType.trim().toUpperCase().replace(' ', '_');
    }
}
