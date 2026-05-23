package com.nuvemite.cms.permits.web.mapper;

import com.nuvemite.cms.permits.domain.Permit;
import com.nuvemite.cms.permits.domain.PermitEligibilityCache;
import com.nuvemite.cms.permits.domain.PermitTimelineEvent;
import com.nuvemite.cms.permits.service.PermitEligibilityService;
import com.nuvemite.cms.permits.web.dto.AssociatedLicenseResponse;
import com.nuvemite.cms.permits.web.dto.PermitResponse;
import com.nuvemite.cms.permits.web.dto.TimelineEventResponse;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class PermitMapper {

    private final PermitEligibilityService eligibilityService;

    public PermitMapper(PermitEligibilityService eligibilityService) {
        this.eligibilityService = eligibilityService;
    }

    public PermitResponse toResponse(Permit permit) {
        return new PermitResponse(
                permit.getId(),
                permit.getPermitNumber(),
                permit.getPermitType(),
                permit.getApplicantCompanyId(),
                permit.getApplicantPremiseId(),
                permit.getApplicantName(),
                permit.getChemicalId(),
                permit.getChemicalName(),
                permit.getBatchId(),
                permit.getMovementId(),
                permit.getQuantity(),
                permit.getUnit(),
                permit.getPurpose(),
                permit.getCountryOfOrigin(),
                permit.getPortOfEntry(),
                permit.getDestinationPremiseId(),
                permit.getSenderPremiseId(),
                permit.getReceiverPremiseId(),
                permit.getTransporterPremiseId(),
                toAssociatedLicense(permit),
                permit.getStatus(),
                permit.getSubmittedAt(),
                permit.getReviewedBy(),
                permit.getReviewedAt(),
                permit.getApprovedAt(),
                permit.getValidFrom(),
                permit.getValidUntil(),
                permit.getConditions(),
                permit.getReviewNotes(),
                permit.getRejectionReason(),
                permit.getCreatedAt(),
                permit.getUpdatedAt(),
                permit.getVersion());
    }

    public TimelineEventResponse toTimelineResponse(PermitTimelineEvent event) {
        return new TimelineEventResponse(
                event.getId(),
                event.getEventType(),
                event.getActorRef(),
                event.getNotes(),
                event.getOccurredAt());
    }

    private AssociatedLicenseResponse toAssociatedLicense(Permit permit) {
        return eligibilityService
                .resolveAssociatedLicense(permit)
                .map(this::toAssociatedLicense)
                .orElse(null);
    }

    private AssociatedLicenseResponse toAssociatedLicense(PermitEligibilityCache cache) {
        LocalDate today = LocalDate.now();
        String status;
        if (!cache.isActiveOn(today)) {
            status = "EXPIRED";
        } else {
            status = "ACTIVE";
        }
        return new AssociatedLicenseResponse(
                cache.getLicenseId(),
                cache.getLicenseNumber(),
                cache.getLicenseType(),
                cache.getValidFrom(),
                cache.getValidUntil(),
                status);
    }
}
