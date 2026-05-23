package com.nuvemite.cms.permits.web;

import com.nuvemite.cms.permits.domain.Permit;
import com.nuvemite.cms.permits.domain.PermitStatus;
import com.nuvemite.cms.permits.domain.PermitType;
import com.nuvemite.cms.permits.security.PermitAccessService;
import com.nuvemite.cms.permits.security.SecurityUtils;
import com.nuvemite.cms.permits.service.PermitService;
import com.nuvemite.cms.permits.web.dto.ApprovePermitRequest;
import com.nuvemite.cms.permits.web.dto.CreatePermitRequest;
import com.nuvemite.cms.permits.web.dto.PermitResponse;
import com.nuvemite.cms.permits.web.dto.RejectPermitRequest;
import com.nuvemite.cms.permits.web.dto.RequestInfoPermitRequest;
import com.nuvemite.cms.permits.web.dto.TimelineEventResponse;
import com.nuvemite.cms.permits.web.dto.UpdatePermitRequest;
import com.nuvemite.cms.permits.web.mapper.PermitMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/permits")
public class PermitController {

    private final PermitService permitService;
    private final PermitAccessService access;
    private final PermitMapper mapper;

    public PermitController(PermitService permitService, PermitAccessService access, PermitMapper mapper) {
        this.permitService = permitService;
        this.access = access;
        this.mapper = mapper;
    }

    @GetMapping
    public Page<PermitResponse> list(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) UUID premiseId,
            @RequestParam(required = false) PermitStatus status,
            @RequestParam(required = false) PermitType permitType,
            @PageableDefault(size = 20) Pageable pageable) {
        var user = SecurityUtils.currentUser();
        UUID filterCompany = companyId;
        if (!user.isRegulator()) {
            filterCompany = companyId != null ? companyId : user.companyIds().stream().findFirst().orElse(null);
        }
        return permitService.list(filterCompany, premiseId, status, permitType, pageable).map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public PermitResponse get(@PathVariable UUID id) {
        Permit permit = permitService.get(id);
        access.requireReadAccess(permit);
        return mapper.toResponse(permit);
    }

    @GetMapping("/{id}/timeline")
    public List<TimelineEventResponse> timeline(@PathVariable UUID id) {
        Permit permit = permitService.get(id);
        access.requireReadAccess(permit);
        return permitService.timeline(id).stream().map(mapper::toTimelineResponse).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PermitResponse create(@Valid @RequestBody CreatePermitRequest request) {
        access.requireCompanyWrite(request.applicantCompanyId(), request.applicantPremiseId());
        return mapper.toResponse(permitService.createDraft(request, SecurityUtils.currentSubject()));
    }

    @PutMapping("/{id}")
    public PermitResponse update(@PathVariable UUID id, @Valid @RequestBody UpdatePermitRequest request) {
        Permit permit = permitService.get(id);
        access.requireCompanyWrite(permit.getApplicantCompanyId(), permit.getApplicantPremiseId());
        return mapper.toResponse(permitService.updateDraft(id, request));
    }

    @PostMapping("/{id}/submit")
    public PermitResponse submit(@PathVariable UUID id) {
        Permit permit = permitService.get(id);
        access.requireCompanySubmit(permit);
        return mapper.toResponse(permitService.submit(id, SecurityUtils.currentSubject()));
    }

    @PostMapping("/{id}/approve")
    public PermitResponse approve(@PathVariable UUID id, @RequestBody(required = false) ApprovePermitRequest request) {
        access.requireRegulator();
        ApprovePermitRequest body = request != null ? request : new ApprovePermitRequest(null, null, null);
        return mapper.toResponse(permitService.approve(id, body, SecurityUtils.currentSubject()));
    }

    @PostMapping("/{id}/reject")
    public PermitResponse reject(@PathVariable UUID id, @Valid @RequestBody RejectPermitRequest request) {
        access.requireRegulator();
        return mapper.toResponse(permitService.reject(id, request, SecurityUtils.currentSubject()));
    }

    @PostMapping("/{id}/request-info")
    public PermitResponse requestInfo(@PathVariable UUID id, @Valid @RequestBody RequestInfoPermitRequest request) {
        access.requireRegulator();
        return mapper.toResponse(permitService.requestInfo(id, request, SecurityUtils.currentSubject()));
    }
}
