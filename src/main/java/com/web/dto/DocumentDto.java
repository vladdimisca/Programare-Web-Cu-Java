package com.web.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.UUID;

public record DocumentDto(
        @Null
        Long id,

        @NotNull
        String identityCard,

        @NotNull
        String medicalCertificate,

        @NotNull
        String diploma,

        @Null
        UUID userId
        ) {
}
