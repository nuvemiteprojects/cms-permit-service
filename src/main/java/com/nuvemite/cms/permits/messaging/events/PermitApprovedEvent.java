package com.nuvemite.cms.permits.messaging.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PermitApprovedEvent(
        UUID eventId,
        String eventType,
        UUID permitId,
        String permitType,
        UUID chemicalId,
        String chemicalName,
        UUID applicantPremiseId,
        UUID destinationPremiseId,
        BigDecimal quantity,
        String unit,
        Instant approvedAt) {}
