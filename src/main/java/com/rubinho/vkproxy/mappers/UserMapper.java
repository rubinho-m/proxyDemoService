package com.rubinho.vkproxy.mappers;

import com.rubinho.vkproxy.dto.SignUpDto;
import com.rubinho.vkproxy.dto.UserDto;
import com.rubinho.vkproxy.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);

    @Mapping(target = "password", ignore = true)
    User signUpToUser(SignUpDto userDto);

    User dtoToUser(UserDto userDto);
}
