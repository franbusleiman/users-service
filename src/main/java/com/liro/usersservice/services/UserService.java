package com.liro.usersservice.services;

import com.liro.usersservice.domain.dtos.users.*;
import com.liro.usersservice.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface UserService {

    UserCompleteResponse findById(Long id);

    UserResponse findByIdentificationNr(String id);

    UserResponse setAccount(SetAccountDTO setAccountDTO);



    UserCompleteResponse findByUsername(String username);
    List<UserCompleteResponse> test();


    UserCompleteResponse findByEmail(String email);

    Boolean existsByEmail(String email);
    Boolean existsByIdentificationNr(String identificationNr);

    Page<UserAnimalsResponse> findAll(Pageable pageable, String param, JwtUserDTO userDTO);

    UserResponse createUser(UserRegister user);

    UserResponse createUserByVet(ClientRegister user, JwtUserDTO userDTO);

    List<UserResponse> createUsersByVetMigrator(List<ClientRegister> user, Long vetClinicId);
    HashMap<String, UserResponse> createUsersByCpVetMigrator(HashMap<String, ClientRegister> user, Long vetClinicId);


    UserResponse changeUserState(User user, Long id);

    UserResponse updateUser(UserDTO user, Long id);


    void sendInviteMail(String email, JwtUserDTO userDTO, Long userId) throws MessagingException, IOException;

    void acceptInvite(String email, String password);
    Boolean existInvite(String email);

    void setFirebaseToken(String firebaseToken, JwtUserDTO userDTO);
    void deleteUser(Long id);
}
