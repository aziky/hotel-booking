package com.nls.userservice.shared.mapper;

import com.nls.userservice.api.dto.request.CreateUserReq;
import com.nls.userservice.api.dto.request.UpdateUserReq;
import com.nls.common.dto.response.UserRes;
import com.nls.userservice.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "name", source = "user", qualifiedByName = "fullName")
    UserRes convertToUserRes(User user);

    User convertCreateUserReqToUser(CreateUserReq request);

    @Named("fullName")
    default String fullName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }

    void updateUserFromDto(UpdateUserReq req, @MappingTarget User user);

}
