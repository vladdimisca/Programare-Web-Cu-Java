package com.web.mapper;

import com.web.dto.UserProgramDto;
import com.web.model.UserProgram;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProgramMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "programId", source = "programOfStudy.id")
    UserProgramDto mapToDto(UserProgram userProgram);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "programOfStudy.id", source = "programId")
    UserProgram mapToEntity(UserProgramDto userProgramDto);
}
