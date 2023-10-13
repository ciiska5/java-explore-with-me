package ru.practicum.user.mapper;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

public class UserMapper {

    public static UserShortDto toUserShortDto(User user) {
        UserShortDto userShortDto = new UserShortDto();

        userShortDto.setId(user.getId());
        userShortDto.setName(user.getName());

        return userShortDto;
    }

    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;
    }

    public static User toUser(NewUserRequest newUserRequest) {
        User user = new User();

        user.setName(newUserRequest.getName());
        user.setEmail(newUserRequest.getEmail());

        return user;
    }
}
