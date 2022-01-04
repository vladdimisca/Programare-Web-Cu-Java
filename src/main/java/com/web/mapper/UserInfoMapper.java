package com.web.mapper;

import com.web.dto.UserInfoDto;
import com.web.model.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {
    @Mapping(target = "country", source = "address.country")
    @Mapping(target = "province", source = "address.province")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "street", source = "address.street")
    @Mapping(target = "number", source = "address.number")
    @Mapping(target = "other", source = "address.other")
    UserInfoDto mapToDto(UserInfo userInfo);

    @Mapping(target = "address.country", source = "country")
    @Mapping(target = "address.province", source = "province")
    @Mapping(target = "address.city", source = "city")
    @Mapping(target = "address.street", source = "street")
    @Mapping(target = "address.number", source = "number")
    @Mapping(target = "address.other", source = "other")
    UserInfo mapToEntity(UserInfoDto userInfoDto);
}
