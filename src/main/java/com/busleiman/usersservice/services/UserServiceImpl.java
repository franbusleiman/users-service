package com.busleiman.usersservice.services;

import brave.Tracer;
import com.busleiman.usersservice.domain.Role;
import com.busleiman.usersservice.domain.User;
import com.busleiman.usersservice.domain.dtos.UserDTO;
import com.busleiman.usersservice.domain.dtos.UserRegister;
import com.busleiman.usersservice.domain.dtos.UserResponse;
import com.busleiman.usersservice.mappers.UserMapper;
import com.busleiman.usersservice.persistance.RoleRepository;
import com.busleiman.usersservice.persistance.UserRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

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
        role.ifPresent(value -> user.setRoles(Arrays.asList(value)));

        user.setEnabled(true);

        return userMapper.userToUserResponse(userRepository.save(user));
    }

    @Override
    public User changeUserState(User user, Long id) {
        User user1 = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        user1.setIntents(user.getIntents());
        user1.setEnabled(user.isEnabled());

            tracer.currentSpan().tag("state.change", String.valueOf(user1.isEnabled()));

        userRepository.save(user1);

        return user1;
    }

    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        userRepository.delete(user);

    }
}
