package com.liro.usersservice.services;

import brave.Tracer;
import com.liro.usersservice.configuration.FeignAnimalsClient;
import com.liro.usersservice.domain.dtos.users.*;
import com.liro.usersservice.domain.model.*;
import com.liro.usersservice.exceptions.UnauthorizedException;
import com.liro.usersservice.mappers.AddressMapper;
import com.liro.usersservice.mappers.UserMapper;
import com.liro.usersservice.persistance.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    FeignAnimalsClient animalsClient;
    @Autowired
    Tracer tracer;
    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    UserMapper userMapper = UserMapper.userMapper;


    @Override
    public UserCompleteResponse findById(Long id) {
        return userMapper.userToUseCompleteResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found")));
    }

    @Override
    public UserResponse findByIdentificationNr(String id) {
        System.out.println("buscando by identification nr: " + id);
        return userMapper.userToUserResponse(userRepository.findUserByIdentificationNr(id)
                .orElseThrow(() -> new RuntimeException("Resource not found")));
    }


    @Override
    public UserCompleteResponse findByUsername(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        return userMapper.userToUseCompleteResponse(user);

    }

    @Override
    public UserCompleteResponse findByEmail(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        return userMapper.userToUseCompleteResponse(user);
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
    public Page<UserAnimalsResponse> findAll(Pageable pageable, String param, JwtUserDTO userDTO) {

        if (!userDTO.getRoles().contains("ROLE_VET")) {

            throw new UnauthorizedException("The user is not authorized!");

        }
        Long vetId = userDTO.getId();
        Boolean lot = false;

        Specification<User> spec = Specification.where(null);

        if (StringUtils.hasText(param)) {

            System.out.println("Encontro param: " + param);
            try {
                System.out.println("Ingreso dni param: " + param);

                Long dni = Long.parseLong(param);
                spec = spec.and(UserSpecifications.hasDni(dni));
                lot = true;
            } catch (NumberFormatException e) {

                System.out.println("Ingreso partes 1: " + param);

                String[] partes = param.split(" ");
                if (partes.length > 1) {
                    System.out.println("Ingreso partes 2: " + param);

                    for (int i = 1; i < partes.length; i++) {

                        String nombre = String.join(" ", Arrays.copyOfRange(partes, 0, partes.length - 1));
                        String apellido = partes[partes.length - 1];
                        spec = spec.or(UserSpecifications.containsName(nombre)
                                .and(UserSpecifications.containsSurname(apellido))
                                .and(UserSpecifications.hasVetId(vetId)));
                        ;
                        lot = true;
                    }
                } else {
                    System.out.println("Ingreso partes 3: " + param);

                    if (param.contains("@")) {
                        System.out.println("Ingreso partes 4: " + param);

                        spec = spec.or(UserSpecifications.containsEmail(param));
                        lot = true;

                    } else {
                        System.out.println("Ingreso partes 5: " + param);

                        spec = spec.or(UserSpecifications.containsName(param))
                                .and(UserSpecifications.hasVetId(vetId));
                        spec = spec.or(UserSpecifications.containsSurname(param))
                                .and(UserSpecifications.hasVetId(vetId));
                        lot = true;

                    }
                }
            }

        }
        System.out.println("Salio con specs: " + spec);

        if (!lot) {

            return Page.empty(pageable);

        }else {
            return userRepository.findAll(spec, pageable)
                .map(user -> {
                    UserAnimalsResponse userAnimalsResponse = userMapper.userToUserAnimalsResponse(user);
                    userAnimalsResponse.setAnimals(animalsClient.getUserAnimals(user.getId()).getBody());
                    return userAnimalsResponse;
                });
        }

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
    public UserResponse createUserByVet(ClientRegister userRegister, String token) {

        JwtUserDTO userDTO = getUser(token);
        VetProfile vetProfile = vetProfileRepository.findByUserId(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        if (!userDTO.getRoles().contains("ROLE_VET")) {

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
    public List<UserResponse> createUsersByVetMigrator(List<ClientRegister> clients, Long vetUserId) {

        VetProfile vetProfile = vetProfileRepository.findByUserId(vetUserId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        List<UserResponse> response = new ArrayList<>();

        clients.forEach(clientRegister -> {

            User user = userMapper.clientRegisterToUser(clientRegister);
            Optional<Role> role = roleRepository.findByName("ROLE_USER");
            role.ifPresent(value -> user.getRoles().add(value));

            user.setEnabled(false);

            if (user.getAddresses() == null) {
                user.setAddresses(new HashSet<>());
            }

            Address address = addressMapper.addressDtoToAddress(clientRegister.getAddress());

            address.setUser(user);
            user.getAddresses().add(address);

            user.setCodigoVetter(vetUserId + "-" + clientRegister.getCodigo());

            VetClient vetClient = VetClient.builder()
                    .vetProfile(vetProfile)
                    .user(user)
                    .accountBalance(clientRegister.getSaldo())
                    .build();

            vetClientRepository.save(vetClient);
            System.out.println("paso: " + userMapper.userToUserResponse(user));
            response.add(userMapper.userToUserResponse(user));
        });

        response.forEach(response1-> System.out.println(response1));
        return response;
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
