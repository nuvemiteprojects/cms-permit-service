package com.nuvemite.cms.permits.security;

import com.nuvemite.cms.permits.domain.Permit;
import com.nuvemite.cms.permits.exception.AccessDeniedException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PermitAccessService {

    public void requireRegulator() {
        if (!SecurityUtils.currentUser().isRegulator()) {
            throw new AccessDeniedException("Regulator role required");
        }
    }

    public void requireReadAccess(Permit permit) {
        CmsUserPrincipal user = SecurityUtils.currentUser();
        if (user.isRegulator()) {
            return;
        }
        if (!user.canAccessPremise(permit.getApplicantCompanyId(), permit.getApplicantPremiseId())) {
            throw new AccessDeniedException("No access to this permit");
        }
    }

    public void requireCompanyWrite(UUID companyId, UUID premiseId) {
        CmsUserPrincipal user = SecurityUtils.currentUser();
        if (user.isRegulator()) {
            return;
        }
        if (!user.canAccessPremise(companyId, premiseId)) {
            throw new AccessDeniedException("No access to premise");
        }
    }

    public void requireCompanySubmit(Permit permit) {
        requireReadAccess(permit);
        if (SecurityUtils.currentUser().isRegulator()) {
            throw new AccessDeniedException("Company users submit permits");
        }
    }
}
