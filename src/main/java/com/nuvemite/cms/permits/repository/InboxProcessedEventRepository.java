package com.nuvemite.cms.permits.repository;

import com.nuvemite.cms.permits.domain.InboxProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxProcessedEventRepository extends JpaRepository<InboxProcessedEvent, InboxProcessedEvent.InboxId> {

    boolean existsByEventIdAndConsumerGroup(String eventId, String consumerGroup);
}
