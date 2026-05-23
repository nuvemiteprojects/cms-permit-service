package com.nuvemite.cms.permits.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuvemite.cms.permits.messaging.events.LicenseGrantedEvent;
import com.nuvemite.cms.permits.messaging.events.LicenseRevokedEvent;
import com.nuvemite.cms.permits.service.InboxService;
import com.nuvemite.cms.permits.service.PermitEligibilityService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LicenseEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(LicenseEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final InboxService inboxService;
    private final PermitEligibilityService eligibilityService;

    public LicenseEventConsumer(
            ObjectMapper objectMapper, InboxService inboxService, PermitEligibilityService eligibilityService) {
        this.objectMapper = objectMapper;
        this.inboxService = inboxService;
        this.eligibilityService = eligibilityService;
    }

    @KafkaListener(topics = EventTypes.LICENSE_GRANTED, groupId = EventTypes.CONSUMER_GROUP)
    public void onLicenseGranted(ConsumerRecord<String, String> record) throws Exception {
        String eventId = header(record, "eventId");
        if (eventId != null && inboxService.isProcessed(eventId)) {
            return;
        }
        LicenseGrantedEvent event = objectMapper.readValue(record.value(), LicenseGrantedEvent.class);
        eligibilityService.handleGranted(event);
        inboxService.markProcessed(eventId != null ? eventId : event.eventId().toString());
        log.debug("Updated eligibility cache for premise {} chemical {}", event.premiseId(), event.chemicalId());
    }

    @KafkaListener(topics = EventTypes.LICENSE_REVOKED, groupId = EventTypes.CONSUMER_GROUP)
    public void onLicenseRevoked(ConsumerRecord<String, String> record) throws Exception {
        String eventId = header(record, "eventId");
        if (eventId != null && inboxService.isProcessed(eventId)) {
            return;
        }
        LicenseRevokedEvent event = objectMapper.readValue(record.value(), LicenseRevokedEvent.class);
        eligibilityService.handleRevoked(event);
        inboxService.markProcessed(eventId != null ? eventId : event.eventId().toString());
        log.debug("Removed eligibility cache for premise {} chemical {}", event.premiseId(), event.chemicalId());
    }

    private static String header(ConsumerRecord<String, String> record, String name) {
        var h = record.headers().lastHeader(name);
        return h != null ? new String(h.value()) : null;
    }
}
