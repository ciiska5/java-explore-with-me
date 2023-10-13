package ru.practicum.user.service;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface AdminUsersService {

    //Получение информации о пользователях
    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    //Добавдение нового пользователя
    UserDto addNewUser(NewUserRequest newUserRequest);

    //Удаление пользователя
    void deleteUser(Long userId);
}
