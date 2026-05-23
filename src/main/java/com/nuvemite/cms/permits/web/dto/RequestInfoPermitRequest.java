package com.nuvemite.cms.permits.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RequestInfoPermitRequest(@NotBlank String notes) {}
