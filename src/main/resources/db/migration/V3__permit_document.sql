CREATE TABLE permit_supporting_document (
  id            UUID PRIMARY KEY,
  permit_id     UUID NOT NULL REFERENCES permit (id) ON DELETE CASCADE,
  document_name VARCHAR(255) NOT NULL,
  document_type VARCHAR(64),
  storage_ref   VARCHAR(512),
  uploaded_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);
