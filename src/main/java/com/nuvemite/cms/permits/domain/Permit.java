package com.nuvemite.cms.permits.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "permit")
public class Permit {

    @Id
    private UUID id;

    @Column(name = "permit_number", nullable = false, unique = true)
    private String permitNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "permit_type", nullable = false)
    private PermitType permitType;

    @Column(name = "applicant_company_id", nullable = false)
    private UUID applicantCompanyId;

    @Column(name = "applicant_premise_id", nullable = false)
    private UUID applicantPremiseId;

    @Column(name = "applicant_name", nullable = false)
    private String applicantName;

    @Column(name = "chemical_id", nullable = false)
    private UUID chemicalId;

    @Column(name = "chemical_name", nullable = false)
    private String chemicalName;

    @Column(name = "batch_id")
    private UUID batchId;

    @Column(name = "movement_id")
    private UUID movementId;

    @Column(precision = 18, scale = 6)
    private BigDecimal quantity;

    private String unit;

    private String purpose;

    @Column(name = "country_of_origin")
    private String countryOfOrigin;

    @Column(name = "port_of_entry")
    private String portOfEntry;

    @Column(name = "destination_premise_id")
    private UUID destinationPremiseId;

    @Column(name = "sender_premise_id")
    private UUID senderPremiseId;

    @Column(name = "receiver_premise_id")
    private UUID receiverPremiseId;

    @Column(name = "transporter_premise_id")
    private UUID transporterPremiseId;

    @Column(name = "license_id")
    private UUID licenseId;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "license_type")
    private String licenseType;

    @Column(name = "license_valid_from")
    private LocalDate licenseValidFrom;

    @Column(name = "license_valid_until")
    private LocalDate licenseValidUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PermitStatus status = PermitStatus.DRAFT;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    private String conditions;

    @Column(name = "review_notes")
    private String reviewNotes;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Version
    private long version;

    protected Permit() {}

    public static Permit createDraft(
            String permitNumber,
            PermitType permitType,
            UUID applicantCompanyId,
            UUID applicantPremiseId,
            String applicantName,
            UUID chemicalId,
            String chemicalName,
            BigDecimal quantity,
            String unit,
            String purpose,
            String countryOfOrigin,
            String portOfEntry,
            UUID destinationPremiseId,
            UUID batchId,
            UUID movementId,
            String createdBy) {
        Permit permit = new Permit();
        permit.id = UUID.randomUUID();
        permit.permitNumber = permitNumber;
        permit.permitType = permitType;
        permit.applicantCompanyId = applicantCompanyId;
        permit.applicantPremiseId = applicantPremiseId;
        permit.applicantName = applicantName;
        permit.chemicalId = chemicalId;
        permit.chemicalName = chemicalName;
        permit.quantity = quantity;
        permit.unit = unit;
        permit.purpose = purpose;
        permit.countryOfOrigin = countryOfOrigin;
        permit.portOfEntry = portOfEntry;
        permit.destinationPremiseId = destinationPremiseId;
        permit.batchId = batchId;
        permit.movementId = movementId;
        permit.createdBy = createdBy;
        Instant now = Instant.now();
        permit.createdAt = now;
        permit.updatedAt = now;
        return permit;
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }

    public void updateDraft(
            BigDecimal quantity,
            String unit,
            String purpose,
            String countryOfOrigin,
            String portOfEntry,
            UUID destinationPremiseId,
            UUID batchId,
            UUID movementId) {
        requireStatus(PermitStatus.DRAFT);
        if (quantity != null) {
            this.quantity = quantity;
        }
        if (unit != null) {
            this.unit = unit;
        }
        if (purpose != null) {
            this.purpose = purpose;
        }
        if (countryOfOrigin != null) {
            this.countryOfOrigin = countryOfOrigin;
        }
        if (portOfEntry != null) {
            this.portOfEntry = portOfEntry;
        }
        if (destinationPremiseId != null) {
            this.destinationPremiseId = destinationPremiseId;
        }
        if (batchId != null) {
            this.batchId = batchId;
        }
        if (movementId != null) {
            this.movementId = movementId;
        }
        touch();
    }

    public void snapshotLicense(
            UUID licenseId,
            String licenseNumber,
            String licenseType,
            LocalDate validFrom,
            LocalDate validUntil) {
        this.licenseId = licenseId;
        this.licenseNumber = licenseNumber;
        this.licenseType = licenseType;
        this.licenseValidFrom = validFrom;
        this.licenseValidUntil = validUntil;
    }

    public void submit() {
        requireStatus(PermitStatus.DRAFT, PermitStatus.REQUIRES_MORE_INFO);
        this.status = PermitStatus.SUBMITTED;
        this.submittedAt = Instant.now();
        touch();
    }

    public void startReview(String reviewer) {
        requireStatus(PermitStatus.SUBMITTED);
        this.status = PermitStatus.UNDER_REVIEW;
        this.reviewedBy = reviewer;
        this.reviewedAt = Instant.now();
        touch();
    }

    public void approve(LocalDate validFrom, LocalDate validUntil, String conditions, String reviewer) {
        requireStatus(PermitStatus.SUBMITTED, PermitStatus.UNDER_REVIEW);
        this.status = PermitStatus.APPROVED;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.conditions = conditions;
        this.reviewedBy = reviewer;
        this.reviewedAt = Instant.now();
        this.approvedAt = Instant.now();
        touch();
    }

    public void reject(String reason, String reviewer) {
        if (status == PermitStatus.APPROVED || status == PermitStatus.REJECTED) {
            throw new IllegalStateException("Cannot reject permit in status " + status);
        }
        this.status = PermitStatus.REJECTED;
        this.rejectionReason = reason;
        this.reviewedBy = reviewer;
        this.reviewedAt = Instant.now();
        touch();
    }

    public void requestMoreInfo(String notes, String reviewer) {
        requireStatus(PermitStatus.SUBMITTED, PermitStatus.UNDER_REVIEW);
        this.status = PermitStatus.REQUIRES_MORE_INFO;
        this.reviewNotes = notes;
        this.reviewedBy = reviewer;
        this.reviewedAt = Instant.now();
        touch();
    }

    private void requireStatus(PermitStatus... allowed) {
        for (PermitStatus s : allowed) {
            if (this.status == s) {
                return;
            }
        }
        throw new IllegalStateException("Invalid status transition from " + status);
    }

    public UUID getId() {
        return id;
    }

    public String getPermitNumber() {
        return permitNumber;
    }

    public PermitType getPermitType() {
        return permitType;
    }

    public UUID getApplicantCompanyId() {
        return applicantCompanyId;
    }

    public UUID getApplicantPremiseId() {
        return applicantPremiseId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public UUID getChemicalId() {
        return chemicalId;
    }

    public String getChemicalName() {
        return chemicalName;
    }

    public UUID getBatchId() {
        return batchId;
    }

    public UUID getMovementId() {
        return movementId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public String getPortOfEntry() {
        return portOfEntry;
    }

    public UUID getDestinationPremiseId() {
        return destinationPremiseId;
    }

    public UUID getSenderPremiseId() {
        return senderPremiseId;
    }

    public UUID getReceiverPremiseId() {
        return receiverPremiseId;
    }

    public UUID getTransporterPremiseId() {
        return transporterPremiseId;
    }

    public UUID getLicenseId() {
        return licenseId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public LocalDate getLicenseValidFrom() {
        return licenseValidFrom;
    }

    public LocalDate getLicenseValidUntil() {
        return licenseValidUntil;
    }

    public PermitStatus getStatus() {
        return status;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public String getConditions() {
        return conditions;
    }

    public String getReviewNotes() {
        return reviewNotes;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public long getVersion() {
        return version;
    }
}
