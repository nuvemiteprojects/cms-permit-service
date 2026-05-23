package com.nuvemite.cms.permits.service;

import com.nuvemite.cms.permits.config.PermitsProperties;
import com.nuvemite.cms.permits.domain.Permit;
import com.nuvemite.cms.permits.domain.PermitStatus;
import com.nuvemite.cms.permits.domain.PermitTimelineEvent;
import com.nuvemite.cms.permits.domain.PermitType;
import com.nuvemite.cms.permits.exception.ResourceNotFoundException;
import com.nuvemite.cms.permits.messaging.EventTypes;
import com.nuvemite.cms.permits.messaging.events.PermitApprovedEvent;
import com.nuvemite.cms.permits.repository.PermitRepository;
import com.nuvemite.cms.permits.repository.PermitTimelineEventRepository;
import com.nuvemite.cms.permits.web.dto.ApprovePermitRequest;
import com.nuvemite.cms.permits.web.dto.CreatePermitRequest;
import com.nuvemite.cms.permits.web.dto.RejectPermitRequest;
import com.nuvemite.cms.permits.web.dto.RequestInfoPermitRequest;
import com.nuvemite.cms.permits.web.dto.UpdatePermitRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermitService {

    private final PermitRepository permitRepository;
    private final PermitTimelineEventRepository timelineRepository;
    private final PermitNumberGenerator numberGenerator;
    private final PermitEligibilityService eligibilityService;
    private final PermitValidationService validationService;
    private final OutboxService outboxService;
    private final PermitsProperties properties;

    public PermitService(
            PermitRepository permitRepository,
            PermitTimelineEventRepository timelineRepository,
            PermitNumberGenerator numberGenerator,
            PermitEligibilityService eligibilityService,
            PermitValidationService validationService,
            OutboxService outboxService,
            PermitsProperties properties) {
        this.permitRepository = permitRepository;
        this.timelineRepository = timelineRepository;
        this.numberGenerator = numberGenerator;
        this.eligibilityService = eligibilityService;
        this.validationService = validationService;
        this.outboxService = outboxService;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public Page<Permit> list(UUID companyId, UUID premiseId, PermitStatus status, PermitType permitType, Pageable pageable) {
        return permitRepository.search(companyId, premiseId, status, permitType, pageable);
    }

    @Transactional(readOnly = true)
    public Permit get(UUID id) {
        return findPermit(id);
    }

    @Transactional(readOnly = true)
    public List<PermitTimelineEvent> timeline(UUID permitId) {
        findPermit(permitId);
        return timelineRepository.findByPermitIdOrderByOccurredAtAsc(permitId);
    }

    @Transactional
    public Permit createDraft(CreatePermitRequest request, String actor) {
        String permitNumber = numberGenerator.next(request.permitType(), permitRepository.count());
        Permit permit = Permit.createDraft(
                permitNumber,
                request.permitType(),
                request.applicantCompanyId(),
                request.applicantPremiseId(),
                request.applicantName(),
                request.chemicalId(),
                request.chemicalName(),
                request.quantity(),
                request.unit(),
                request.purpose(),
                request.countryOfOrigin(),
                request.portOfEntry(),
                request.destinationPremiseId(),
                request.batchId(),
                request.movementId(),
                actor);
        permitRepository.save(permit);
        recordTimeline(permit.getId(), "PERMIT_CREATED", actor, "Permit draft created");
        return permit;
    }

    @Transactional
    public Permit updateDraft(UUID id, UpdatePermitRequest request) {
        Permit permit = findPermit(id);
        permit.updateDraft(
                request.quantity(),
                request.unit(),
                request.purpose(),
                request.countryOfOrigin(),
                request.portOfEntry(),
                request.destinationPremiseId(),
                request.batchId(),
                request.movementId());
        return permitRepository.save(permit);
    }

    @Transactional
    public Permit submit(UUID id, String actor) {
        Permit permit = findPermit(id);
        validationService.validateForSubmit(permit);
        eligibilityService.requireForSubmit(permit);
        permit.submit();
        permitRepository.save(permit);
        recordTimeline(permit.getId(), "PERMIT_SUBMITTED", actor, null);
        return permit;
    }

    @Transactional
    public Permit approve(UUID id, ApprovePermitRequest request, String actor) {
        Permit permit = findPermit(id);
        permit.startReview(actor);
        LocalDate validFrom = request.validFrom() != null ? request.validFrom() : LocalDate.now();
        LocalDate validUntil = request.validUntil() != null
                ? request.validUntil()
                : validFrom.plusYears(properties.permitValidityYears());
        permit.approve(validFrom, validUntil, request.conditions(), actor);
        permitRepository.save(permit);
        recordTimeline(permit.getId(), "PERMIT_APPROVED", actor, request.conditions());

        UUID eventId = UUID.randomUUID();
        outboxService.enqueue(
                "permit",
                permit.getId(),
                EventTypes.PERMIT_APPROVED,
                new PermitApprovedEvent(
                        eventId,
                        EventTypes.PERMIT_APPROVED,
                        permit.getId(),
                        permit.getPermitType().name(),
                        permit.getChemicalId(),
                        permit.getChemicalName(),
                        permit.getApplicantPremiseId(),
                        permit.getDestinationPremiseId(),
                        permit.getQuantity(),
                        permit.getUnit(),
                        permit.getApprovedAt()));
        return permit;
    }

    @Transactional
    public Permit reject(UUID id, RejectPermitRequest request, String actor) {
        Permit permit = findPermit(id);
        permit.reject(request.reason(), actor);
        permitRepository.save(permit);
        recordTimeline(permit.getId(), "PERMIT_REJECTED", actor, request.reason());
        return permit;
    }

    @Transactional
    public Permit requestInfo(UUID id, RequestInfoPermitRequest request, String actor) {
        Permit permit = findPermit(id);
        permit.requestMoreInfo(request.notes(), actor);
        permitRepository.save(permit);
        recordTimeline(permit.getId(), "REQUIRES_MORE_INFO", actor, request.notes());
        return permit;
    }

    private Permit findPermit(UUID id) {
        return permitRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Permit not found: " + id));
    }

    private void recordTimeline(UUID permitId, String eventType, String actor, String notes) {
        timelineRepository.save(PermitTimelineEvent.create(permitId, eventType, actor, notes));
    }
}
