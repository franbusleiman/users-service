package com.liro.usersservice.services;

import brave.Tracer;
import com.liro.usersservice.domain.dtos.users.UserCompleteResponse;
import com.liro.usersservice.domain.model.Role;
import com.liro.usersservice.domain.model.User;
import com.liro.usersservice.domain.dtos.users.UserRegister;
import com.liro.usersservice.domain.dtos.users.UserResponse;
import com.liro.usersservice.mappers.UserMapper;
import com.liro.usersservice.persistance.RoleRepository;
import com.liro.usersservice.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    Tracer tracer;


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    UserMapper userMapper = UserMapper.userMapper;


    @Override
    public UserResponse findById(Long id) {
        return userMapper.userToUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found")));
    }

    @Override
    public UserCompleteResponse findByUsername(String username) {
        User user =  userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        return userMapper.userToUseCompleteResponse(user);

    }

    @Override
    public UserResponse findByEmail(String email) {
        User user =  userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        return userMapper.userToUserResponse(user);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Boolean existsByIdentificationNr(String identificationNr) {
        return userRepository.existsByIdentificationNr(identificationNr);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> userMapper.userToUserResponse(user))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse createUser(UserRegister userRegister) {

        User user = userMapper.userRegisterToUser(userRegister);

        user.setPassword(passwordEncoder.encode(userRegister.getPassword()));

        Optional<Role> role = roleRepository.findByName("ROLE_USER");

        role.ifPresent(value -> user.getRoles().add(value));

        user.setEnabled(true);

        return userMapper.userToUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse changeUserState(User user, Long id) {
        User user1 = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        user1.setIntents(user.getIntents());
        user1.setEnabled(user.isEnabled());

            tracer.currentSpan().tag("state.change", String.valueOf(user1.isEnabled()));

        userRepository.save(user1);

        return userMapper.userToUserResponse(user1);
    }

    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        userRepository.delete(user);

    }
}
