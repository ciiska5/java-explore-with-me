package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EmailAlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminUsersServiceImpl implements AdminUsersService {
    private final UserRepository userRepository;

    //Получение информации о пользователях
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        log.info("Получены пользователи по парметрам: ids = {}, from = {}, size = {}", ids, from, size);

        if (ids == null) {
            return userRepository.findAll(pageable)
                    .stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }

        return userRepository.findAllByIdIn(ids, pageable)
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

    }

    //Добавдение нового пользователя
    @Override
    @Transactional
    public UserDto addNewUser(NewUserRequest newUserRequest) {
        String email = newUserRequest.getEmail();

        if(userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Пользователь с таким email уже зарегистрирован");
        }

        User user = UserMapper.toUser(newUserRequest);
        user = userRepository.save(user);

        log.info("Добавлен новый пользователь с id = {}", user.getId());

        return UserMapper.toUserDto(user);
    }

    //Удаление пользователя
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Удаление пользователя с id = {}", userId);
        User user = checkUserExistence(userId);

        userRepository.delete(user);
    }

    //проверка существования пользователя
    private User checkUserExistence(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }
}