package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.AdminUsersService;

import javax.validation.Valid;
import java.util.List;

/**
 * API для работы с пользователями
 */

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class AdminUsersController {
    private final AdminUsersService adminUsersService;

    //Получение информации о пользователях
    @GetMapping
    public List<UserDto> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return adminUsersService.getUsers(ids, from, size);
    }

    //Добавдение нового пользователя
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addNewUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        return adminUsersService.addNewUser(newUserRequest);
    }

    //Удаление пользователя
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        adminUsersService.deleteUser(userId);
    }
}
