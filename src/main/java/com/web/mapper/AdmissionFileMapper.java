package com.web.mapper;

import com.web.dto.AdmissionFileDto;
import com.web.model.AdmissionFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdmissionFileMapper {

    @Mapping(target = "userId", source = "user.id")
    AdmissionFileDto mapToDto(AdmissionFile admissionFile);

    @Mapping(target = "user.id", source = "userId")
    AdmissionFile mapToEntity(AdmissionFileDto admissionFileDto);
}
