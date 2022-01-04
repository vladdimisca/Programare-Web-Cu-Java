package com.web.dto;

import com.web.model.enumeration.AdmissionFileStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdmissionFileDto(
        Long id,

        LocalDateTime submittedAt,

        AdmissionFileStatus status,

        UUID userId
        ) {
}
