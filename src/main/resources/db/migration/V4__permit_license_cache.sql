CREATE TABLE permit_eligibility_cache (
  premise_id      UUID NOT NULL,
  chemical_id     UUID NOT NULL,
  license_type    VARCHAR(64) NOT NULL,
  license_id      UUID NOT NULL,
  license_number  VARCHAR(64) NOT NULL,
  valid_from      DATE NOT NULL,
  valid_until     DATE NOT NULL,
  refreshed_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (premise_id, chemical_id, license_type)
);

CREATE INDEX idx_pec_premise ON permit_eligibility_cache (premise_id);
CREATE INDEX idx_pec_chemical ON permit_eligibility_cache (chemical_id);
