package com.nuvemite.cms.permits.web.dto;

import java.time.LocalDate;

public record ApprovePermitRequest(LocalDate validFrom, LocalDate validUntil, String conditions) {}
