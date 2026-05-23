package com.nuvemite.cms.permits.web.dto;

import com.nuvemite.cms.permits.domain.PermitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record CreatePermitRequest(
        @NotNull PermitType permitType,
        @NotNull UUID applicantCompanyId,
        @NotNull UUID applicantPremiseId,
        @NotBlank String applicantName,
        @NotNull UUID chemicalId,
        @NotBlank String chemicalName,
        BigDecimal quantity,
        String unit,
        String purpose,
        String countryOfOrigin,
        String portOfEntry,
        UUID destinationPremiseId,
        UUID batchId,
        UUID movementId) {}
