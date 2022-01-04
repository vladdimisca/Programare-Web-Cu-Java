package com.web.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.UUID;

public record UserProgramDto(
        @Null
        Long id,

        @Null
        UUID userId,

        @NotNull
        UUID programId,

        @Null
        Integer grade
        ) {
}
