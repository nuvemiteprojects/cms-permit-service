package com.nuvemite.cms.permits.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "permit_timeline_event")
public class PermitTimelineEvent {

    @Id
    private UUID id;

    @Column(name = "permit_id", nullable = false)
    private UUID permitId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "actor_ref")
    private String actorRef;

    private String notes;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected PermitTimelineEvent() {}

    public static PermitTimelineEvent create(UUID permitId, String eventType, String actorRef, String notes) {
        PermitTimelineEvent event = new PermitTimelineEvent();
        event.id = UUID.randomUUID();
        event.permitId = permitId;
        event.eventType = eventType;
        event.actorRef = actorRef;
        event.notes = notes;
        event.occurredAt = Instant.now();
        return event;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPermitId() {
        return permitId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getActorRef() {
        return actorRef;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
