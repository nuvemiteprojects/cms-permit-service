package com.nuvemite.cms.permits.repository;

import com.nuvemite.cms.permits.domain.PermitTimelineEvent;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermitTimelineEventRepository extends JpaRepository<PermitTimelineEvent, UUID> {

    List<PermitTimelineEvent> findByPermitIdOrderByOccurredAtAsc(UUID permitId);
}
