package com.qlsv.dkmh.mapper;

import com.qlsv.dkmh.dto.request.UserRequest;
import com.qlsv.dkmh.dto.response.UserResponse;
import com.qlsv.dkmh.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    User toEntity (UserRequest request);
    UserResponse toResponse (User user);
    void update (@MappingTarget User user, UserRequest request);
}
