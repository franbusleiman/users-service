package com.liro.usersservice.services;

import com.liro.usersservice.domain.model.User;
import com.liro.usersservice.domain.dtos.UserRegister;
import com.liro.usersservice.domain.dtos.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse findById(Long id);

    UserResponse findByUsername(String username);

    UserResponse findByEmail(String email);

    Boolean existsByEmail(String email);
    Boolean existsByIdentificationNr(String identificationNr);

    List<UserResponse> findAll();

    UserResponse createUser(UserRegister user);

    UserResponse changeUserState(User user, Long id);

    void deleteUser(Long id);
}
