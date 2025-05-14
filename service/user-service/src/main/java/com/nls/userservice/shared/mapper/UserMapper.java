package com.nls.userservice.shared.mapper;

import com.nls.userservice.api.dto.response.UserRes;
import com.nls.userservice.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserRes convertToUserRes(User user);

}
