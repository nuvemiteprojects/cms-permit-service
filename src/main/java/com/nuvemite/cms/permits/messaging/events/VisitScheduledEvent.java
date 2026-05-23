package com.nuvemite.cms.permits.messaging.events;

import java.time.LocalDate;
import java.util.UUID;

public record VisitScheduledEvent(
        UUID eventId,
        UUID applicationId,
        UUID premiseId,
        LocalDate visitDate,
        String inspectorName) {}
