package com.busleiman.usersservice.services;

import com.busleiman.usersservice.domain.User;

import java.util.List;

public interface UserService {

    User findById(Long id);

    User findByUsername(String username);

    List<User> findAll();

    User createUser(User user);

    User changeUserState(User user, Long id);

    void deleteUser(Long id);
}
