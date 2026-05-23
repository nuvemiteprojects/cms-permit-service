package com.nuvemite.cms.permits.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nuvemite.cms.permits.messaging.events.PermitApprovedEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PermitApprovedOutboxTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void serializesPermitApprovedEventForChemicalMgmt() throws Exception {
        PermitApprovedEvent event = new PermitApprovedEvent(
                UUID.randomUUID(),
                EventTypes.PERMIT_APPROVED,
                UUID.randomUUID(),
                "IMPORT",
                UUID.randomUUID(),
                "Acetone",
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(100),
                "kg",
                Instant.parse("2026-05-17T12:00:00Z"));

        String json = objectMapper.writeValueAsString(event);
        PermitApprovedEvent parsed = objectMapper.readValue(json, PermitApprovedEvent.class);

        assertThat(parsed.permitType()).isEqualTo("IMPORT");
        assertThat(parsed.quantity()).isEqualByComparingTo("100");
        assertThat(parsed.eventType()).isEqualTo(EventTypes.PERMIT_APPROVED);
    }
}
