package com.nuvemite.cms.permits.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "permit_eligibility_cache")
@IdClass(PermitEligibilityCache.Pk.class)
public class PermitEligibilityCache {

    @Id
    @Column(name = "premise_id", nullable = false)
    private UUID premiseId;

    @Id
    @Column(name = "chemical_id", nullable = false)
    private UUID chemicalId;

    @Id
    @Column(name = "license_type", nullable = false)
    private String licenseType;

    @Column(name = "license_id", nullable = false)
    private UUID licenseId;

    @Column(name = "license_number", nullable = false)
    private String licenseNumber;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;

    @Column(name = "refreshed_at", nullable = false)
    private Instant refreshedAt;

    protected PermitEligibilityCache() {}

    public static PermitEligibilityCache fromGrant(
            UUID premiseId,
            UUID chemicalId,
            String licenseType,
            UUID licenseId,
            String licenseNumber,
            LocalDate validFrom,
            LocalDate validUntil) {
        PermitEligibilityCache cache = new PermitEligibilityCache();
        cache.premiseId = premiseId;
        cache.chemicalId = chemicalId;
        cache.licenseType = licenseType;
        cache.licenseId = licenseId;
        cache.licenseNumber = licenseNumber;
        cache.validFrom = validFrom;
        cache.validUntil = validUntil;
        cache.refreshedAt = Instant.now();
        return cache;
    }

    public void applyGrant(
            UUID licenseId,
            String licenseNumber,
            LocalDate validFrom,
            LocalDate validUntil) {
        this.licenseId = licenseId;
        this.licenseNumber = licenseNumber;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.refreshedAt = Instant.now();
    }

    public boolean isActiveOn(LocalDate date) {
        return !validUntil.isBefore(date);
    }

    public UUID getPremiseId() {
        return premiseId;
    }

    public UUID getChemicalId() {
        return chemicalId;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public UUID getLicenseId() {
        return licenseId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public Instant getRefreshedAt() {
        return refreshedAt;
    }

    public static class Pk implements Serializable {
        private UUID premiseId;
        private UUID chemicalId;
        private String licenseType;

        public Pk() {}

        public Pk(UUID premiseId, UUID chemicalId, String licenseType) {
            this.premiseId = premiseId;
            this.chemicalId = chemicalId;
            this.licenseType = licenseType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Pk pk)) {
                return false;
            }
            return Objects.equals(premiseId, pk.premiseId)
                    && Objects.equals(chemicalId, pk.chemicalId)
                    && Objects.equals(licenseType, pk.licenseType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(premiseId, chemicalId, licenseType);
        }
    }
}
