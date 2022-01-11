package com.web.mapper;

import com.web.dto.UserDto;
import com.web.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = { UserInfoMapper.class }, componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "documentId", source = "document.id")
    @Mapping(target = "admissionFileId", source = "admissionFile.id")
    @Mapping(target = "password", expression = "java(null)")
    UserDto mapToDto(User user);

    @Mapping(target = "document.id", source = "documentId")
    @Mapping(target = "admissionFile.id", source = "admissionFileId")
    User mapToEntity(UserDto userDTO);
}
