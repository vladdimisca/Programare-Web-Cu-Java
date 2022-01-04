package com.web.dto;

import com.web.model.enumeration.FinancingType;
import com.web.model.enumeration.ProgramType;

import javax.validation.constraints.*;
import java.util.UUID;

public record ProgramOfStudyDto(
        @Null
        UUID id,

        @Size(min = 2)
        String name,

        @NotNull
        ProgramType type,

        @Min(1)
        @Max(10)
        Integer numberOfYears,

        @Min(50)
        @Max(400)
        Integer numberOfStudents,

        @NotNull
        FinancingType financingType
        ) {
}
