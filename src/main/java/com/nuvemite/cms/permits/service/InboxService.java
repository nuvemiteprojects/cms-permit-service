package com.nuvemite.cms.permits.service;

import com.nuvemite.cms.permits.domain.InboxProcessedEvent;
import com.nuvemite.cms.permits.messaging.EventTypes;
import com.nuvemite.cms.permits.repository.InboxProcessedEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InboxService {

    private final InboxProcessedEventRepository repository;

    public InboxService(InboxProcessedEventRepository repository) {
        this.repository = repository;
    }

    public boolean isProcessed(String eventId) {
        return repository.existsByEventIdAndConsumerGroup(eventId, EventTypes.CONSUMER_GROUP);
    }

    @Transactional
    public void markProcessed(String eventId) {
        if (!repository.existsByEventIdAndConsumerGroup(eventId, EventTypes.CONSUMER_GROUP)) {
            repository.save(InboxProcessedEvent.create(eventId, EventTypes.CONSUMER_GROUP));
        }
    }
}
