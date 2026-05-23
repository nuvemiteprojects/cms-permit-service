CREATE TABLE permit_timeline_event (
  id          UUID PRIMARY KEY,
  permit_id   UUID NOT NULL REFERENCES permit (id) ON DELETE CASCADE,
  event_type  VARCHAR(64) NOT NULL,
  actor_ref   VARCHAR(128),
  notes       TEXT,
  occurred_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_permit_timeline_permit ON permit_timeline_event (permit_id);
