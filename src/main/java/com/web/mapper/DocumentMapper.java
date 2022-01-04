package com.web.mapper;

import com.web.dto.DocumentDto;
import com.web.model.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "userId", source = "user.id")
    DocumentDto mapToDto(Document document);

    @Mapping(target = "user.id", source = "userId")
    Document mapToEntity(DocumentDto documentDto);
}
