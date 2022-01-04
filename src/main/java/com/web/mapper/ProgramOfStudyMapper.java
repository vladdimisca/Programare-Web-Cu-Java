package com.web.mapper;

import com.web.dto.ProgramOfStudyDto;
import com.web.model.ProgramOfStudy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProgramOfStudyMapper {

    ProgramOfStudyDto mapToDto(ProgramOfStudy programOfStudy);

    ProgramOfStudy mapToEntity(ProgramOfStudyDto programOfStudyDto);
}
