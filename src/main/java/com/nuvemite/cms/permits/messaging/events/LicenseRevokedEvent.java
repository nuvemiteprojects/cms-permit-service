package com.nuvemite.cms.permits.messaging.events;

import java.util.UUID;

public record LicenseRevokedEvent(
        UUID eventId,
        UUID licenseId,
        UUID companyId,
        UUID premiseId,
        String licenseType,
        UUID chemicalId) {}
