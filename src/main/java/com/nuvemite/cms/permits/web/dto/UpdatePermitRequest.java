package com.nuvemite.cms.permits.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdatePermitRequest(
        BigDecimal quantity,
        String unit,
        String purpose,
        String countryOfOrigin,
        String portOfEntry,
        UUID destinationPremiseId,
        UUID batchId,
        UUID movementId) {}
