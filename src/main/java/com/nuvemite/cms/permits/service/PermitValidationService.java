package com.nuvemite.cms.permits.service;

import com.nuvemite.cms.permits.config.PermitsProperties;
import com.nuvemite.cms.permits.domain.Permit;
import com.nuvemite.cms.permits.exception.ServiceUnavailableException;
import com.nuvemite.cms.permits.exception.UnprocessableEntityException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class PermitValidationService {

    private final RestClient restClient;
    private final PermitsProperties properties;

    public PermitValidationService(RestClient.Builder restClientBuilder, PermitsProperties properties) {
        this.restClient = restClientBuilder.build();
        this.properties = properties;
    }

    public void validateForSubmit(Permit permit) {
        validatePremiseActive(permit.getApplicantPremiseId());
        validateChemicalActive(permit.getChemicalId());
    }

    private void validatePremiseActive(java.util.UUID premiseId) {
        try {
            PremiseView premise = restClient
                    .get()
                    .uri(properties.services().companiesUrl() + "/api/v1/premises/{id}", premiseId)
                    .retrieve()
                    .body(PremiseView.class);
            if (premise == null || !"ACTIVE".equalsIgnoreCase(premise.status())) {
                throw new UnprocessableEntityException("Applicant premise is not active.");
            }
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("Companies service unavailable for premise validation", e);
        }
    }

    private void validateChemicalActive(java.util.UUID chemicalId) {
        try {
            ChemicalView chemical = restClient
                    .get()
                    .uri(properties.services().chemicalManagementUrl() + "/api/v1/chemicals/{id}", chemicalId)
                    .retrieve()
                    .body(ChemicalView.class);
            if (chemical == null) {
                throw new UnprocessableEntityException("Chemical was not found.");
            }
            if (chemical.registryStatus() != null && !"ACTIVE".equalsIgnoreCase(chemical.registryStatus())) {
                throw new UnprocessableEntityException(
                        "Permit submission blocked: chemical registry status is not Active.");
            }
            if ("BANNED".equalsIgnoreCase(chemical.restrictionStatus())) {
                throw new UnprocessableEntityException("Permit submission blocked: chemical is banned.");
            }
        } catch (RestClientException e) {
            throw new ServiceUnavailableException(
                    "Chemical management service unavailable for chemical validation", e);
        }
    }

    record PremiseView(String status) {}

    record ChemicalView(String registryStatus, String restrictionStatus) {}
}
