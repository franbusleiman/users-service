package com.liro.usersservice.services;

import com.liro.usersservice.domain.dtos.users.*;
import com.liro.usersservice.domain.model.User;
import org.graalvm.shadowed.org.jcodings.util.Hash;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;

public interface UserService {

    UserCompleteResponse findById(Long id);

    UserResponse findByIdentificationNr(String id);


    UserCompleteResponse findByUsername(String username);

    UserCompleteResponse findByEmail(String email);

    Boolean existsByEmail(String email);
    Boolean existsByIdentificationNr(String identificationNr);

    Page<UserAnimalsResponse> findAll(Pageable pageable, String param, JwtUserDTO userDTO);

    UserResponse createUser(UserRegister user);

    UserResponse createUserByVet(ClientRegister user, JwtUserDTO userDTO);

    List<UserResponse> createUsersByVetMigrator(List<ClientRegister> user, Long vetClinicId);
    HashMap<String, UserResponse> createUsersByCpVetMigrator(HashMap<String, ClientRegister> user, Long vetClinicId);


    UserResponse changeUserState(User user, Long id);

    void deleteUser(Long id);
}
