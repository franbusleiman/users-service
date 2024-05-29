package com.liro.usersservice.services;

import com.liro.usersservice.domain.model.User;
import com.liro.usersservice.domain.dtos.UserRegister;
import com.liro.usersservice.domain.dtos.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse findById(Long id);

    User findByUsername(String username);

    User findByEmail(String email);

    List<UserResponse> findAll();

    UserResponse createUser(UserRegister user);

    User changeUserState(User user, Long id);

    void deleteUser(Long id);
}
