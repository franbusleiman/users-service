package com.liro.usersservice.services;

import com.liro.usersservice.domain.dtos.users.*;
import com.liro.usersservice.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserCompleteResponse findById(Long id);

    UserResponse findByIdentificationNr(String id);


    UserCompleteResponse findByUsername(String username);

    UserResponse findByEmail(String email);

    Boolean existsByEmail(String email);
    Boolean existsByIdentificationNr(String identificationNr);

    Page<UserAnimalsResponse> findAll(Pageable pageable, String param, JwtUserDTO userDTO);

    UserResponse createUser(UserRegister user);

    UserResponse createUserByVet(ClientRegister user, String token);

    Void createUsersByVetMigrator(List<ClientRegister> user, Long vetUserId);


    UserResponse changeUserState(User user, Long id);

    void deleteUser(Long id);
}
