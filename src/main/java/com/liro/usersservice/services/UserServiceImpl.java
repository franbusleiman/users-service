package com.liro.usersservice.services;

import brave.Tracer;
import com.liro.usersservice.domain.dtos.users.*;
import com.liro.usersservice.domain.model.*;
import com.liro.usersservice.exceptions.UnauthorizedException;
import com.liro.usersservice.mappers.AddressMapper;
import com.liro.usersservice.mappers.UserMapper;
import com.liro.usersservice.persistance.RoleRepository;
import com.liro.usersservice.persistance.UserRepository;
import com.liro.usersservice.persistance.VetClientRepository;
import com.liro.usersservice.persistance.VetProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.liro.usersservice.services.Util.getUser;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    VetProfileRepository vetProfileRepository;

    @Autowired
    VetClientRepository vetClientRepository;


    @Autowired
    RoleRepository roleRepository;

    @Autowired
    Tracer tracer;
    @Autowired
    private  AddressMapper addressMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    UserMapper userMapper = UserMapper.userMapper;





    @Override
    public UserCompleteResponse findById(Long id) {
        return userMapper.userToUseCompleteResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found")));
    }

    @Override
    public UserResponse findByIdentificationNr(String id){
        System.out.println("buscando by identification nr: " + id);
        return userMapper.userToUserResponse(userRepository.findUserByIdentificationNr(id)
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
    public UserResponse createUserByVet(ClientRegister userRegister, String token){

        JwtUserDTO userDTO = getUser(token);
        VetProfile vetProfile = vetProfileRepository.findById(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        if (!userDTO.getRoles().contains("ROLE_VET")){

            throw new UnauthorizedException("The user is not authorized!");

        }

        User user = userMapper.clientRegisterToUser(userRegister);
        Optional<Role> role = roleRepository.findByName("ROLE_USER");
        role.ifPresent(value -> user.getRoles().add(value));

        user.setEnabled(false);

        if (user.getAddresses() == null) {
            user.setAddresses(new HashSet<>());
        }

        Address address = addressMapper.addressDtoToAddress(userRegister.getAddress());

        address.setUser(user);
        user.getAddresses().add(address);

        VetClient vetClient = VetClient.builder()
                .vetProfile(vetProfile)
                .user(user)
                .accountBalance(0.0)
                .build();

        vetClientRepository.save(vetClient);

        return userMapper.userToUserResponse(user);
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
