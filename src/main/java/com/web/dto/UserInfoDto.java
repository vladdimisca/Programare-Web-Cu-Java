package com.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.web.model.enumeration.CivilStatus;
import com.web.model.enumeration.SexType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public record UserInfoDto(
        @NotNull
        @Size(min = 2)
        String firstName,

        @NotNull
        @Size(min = 2)
        String lastName,

        @Pattern(regexp = "([12]|[56])\\d{12}", message = "must be 13 digits long, starting with a value from {1, 2, 5, 6}!")
        String cnp,

        @NotBlank
        String nationality,

        @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$")
        String phoneNumber,

        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        LocalDate birthDate,

        @NotNull
        CivilStatus civilStatus,

        @NotNull
        SexType sex,

        @NotBlank
        String country,

        @NotBlank
        String province,

        @NotBlank
        String city,

        @NotBlank
        String street,

        @NotBlank
        String number,

        String other
        ) {
}
