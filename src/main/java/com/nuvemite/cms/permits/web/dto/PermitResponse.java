package com.nuvemite.cms.permits.web.dto;

import com.nuvemite.cms.permits.domain.PermitStatus;
import com.nuvemite.cms.permits.domain.PermitType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PermitResponse(
        UUID id,
        String permitNumber,
        PermitType permitType,
        UUID applicantCompanyId,
        UUID applicantPremiseId,
        String applicantName,
        UUID chemicalId,
        String chemicalName,
        UUID batchId,
        UUID movementId,
        BigDecimal quantity,
        String unit,
        String purpose,
        String countryOfOrigin,
        String portOfEntry,
        UUID destinationPremiseId,
        UUID senderPremiseId,
        UUID receiverPremiseId,
        UUID transporterPremiseId,
        AssociatedLicenseResponse associatedLicense,
        PermitStatus status,
        Instant submittedAt,
        String reviewedBy,
        Instant reviewedAt,
        Instant approvedAt,
        LocalDate validFrom,
        LocalDate validUntil,
        String conditions,
        String reviewNotes,
        String rejectionReason,
        Instant createdAt,
        Instant updatedAt,
        long version) {}
