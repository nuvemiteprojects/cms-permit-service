package com.nuvemite.cms.permits.service;

import com.nuvemite.cms.permits.domain.PermitType;
import org.springframework.stereotype.Component;

@Component
public class PermitNumberGenerator {

    private static final java.util.Map<PermitType, String> PREFIX = java.util.Map.of(
            PermitType.IMPORT, "IMP",
            PermitType.EXPORT, "EXP",
            PermitType.MANUFACTURING, "MFG",
            PermitType.STORAGE, "STO",
            PermitType.TRANSPORT, "TRN",
            PermitType.USE, "USE",
            PermitType.WASTE_HANDLING, "WST",
            PermitType.DISPOSAL, "DSP");

    public String next(PermitType permitType, long sequence) {
        String prefix = PREFIX.getOrDefault(permitType, "PRM");
        int year = java.time.Year.now().getValue();
        return "%s-%d-%04d".formatted(prefix, year, sequence + 1);
    }
}
