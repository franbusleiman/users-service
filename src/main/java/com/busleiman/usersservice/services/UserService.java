package com.busleiman.usersservice.services;

import com.busleiman.usersservice.domain.User;
import com.busleiman.usersservice.domain.dtos.UserDTO;
import com.busleiman.usersservice.domain.dtos.UserRegister;
import com.busleiman.usersservice.domain.dtos.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse findById(Long id);

    User findByUsername(String username);

    List<UserResponse> findAll();

    UserResponse createUser(UserRegister user);

    User changeUserState(User user, Long id);

    void deleteUser(Long id);

    List<UserResponse> getUsersByLatLongAndDistance(Long latitude, Long longitude, Long km);
}
