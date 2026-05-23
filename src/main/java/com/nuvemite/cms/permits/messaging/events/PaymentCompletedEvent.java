package com.nuvemite.cms.permits.messaging.events;

import java.util.UUID;

public record PaymentCompletedEvent(
        UUID eventId,
        String referenceType,
        UUID referenceId,
        UUID companyId,
        UUID invoiceId) {}
