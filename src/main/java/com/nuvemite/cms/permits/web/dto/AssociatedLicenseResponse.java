package com.nuvemite.cms.permits.web.dto;

import java.time.LocalDate;
import java.util.UUID;

public record AssociatedLicenseResponse(
        UUID licenseId,
        String licenseNumber,
        String licenseType,
        LocalDate validFrom,
        LocalDate validUntil,
        String status) {}
