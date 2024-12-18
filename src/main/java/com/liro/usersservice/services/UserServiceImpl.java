package com.liro.usersservice.services;

import brave.Tracer;
import com.liro.usersservice.configuration.FeignAnimalsClient;
import com.liro.usersservice.configuration.FeignClinicClientClient;
import com.liro.usersservice.domain.dtos.animals.AnimalCompleteResponse;
import com.liro.usersservice.domain.dtos.users.*;
import com.liro.usersservice.domain.model.*;
import com.liro.usersservice.exceptions.UnauthorizedException;
import com.liro.usersservice.mappers.AddressMapper;
import com.liro.usersservice.mappers.UserMapper;
import com.liro.usersservice.persistance.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.charset.StandardCharsets;

import java.util.*;
import java.util.stream.Collectors;



@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    VetProfileRepository vetProfileRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    FeignAnimalsClient animalsClient;

    @Autowired
    Tracer tracer;

    @Autowired
    FeignClinicClientClient clinicsClient;
    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    UserMapper userMapper = UserMapper.userMapper;


    @Override
    public UserCompleteResponse findById(Long id) {
        return userMapper.userToUseCompleteResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found")));
    }

    @Override
    public UserResponse findByIdentificationNr(String id) {
        return userMapper.userToUserResponse(userRepository.findUserByIdentificationNr(id)
                .orElseThrow(() -> new RuntimeException("Resource not found")));
    }

    @Override
    public UserResponse setAccount(SetAccountDTO setAccountDTO) {

        User user = userRepository.findUserByEmail(setAccountDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        if (user.getPassword() == null) {
            user.setPassword(passwordEncoder.encode(setAccountDTO.getPassword()));
        }

        return userMapper.userToUserResponse(userRepository.save(user));
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

        Long clinicId = userDTO.getClinicId(); // Sacar el clinicId en vez de vetId
        if (clinicId == null) {
            throw new IllegalArgumentException("Clinic ID is missing in user data!");
        }

        // TODO when searching by name go first to clinic ms (gateway logic)
        List<Long> clinicUserIds;

        if (StringUtils.isBlank(param)) {
            return Page.empty();
        }
        Specification<User> spec = Specification.where(null);


        if (StringUtils.isNotBlank(param)) {
            try {
                Long dni = Long.parseLong(param);
                spec = spec.and(UserSpecifications.hasDni(dni));
            } catch (NumberFormatException e) {

                String[] partes = param.split(" ");
                if (partes.length <= 1) {
                    if (param.contains("@")) {
                        spec = spec.or(UserSpecifications.containsEmail(param));
                    } else {
                        clinicUserIds = clinicsClient.getUsersByClinicId(clinicId).getBody();
                        spec = spec.or(UserSpecifications.containsName(param))
                                .and(UserSpecifications.hasIdIn(clinicUserIds));
                        spec = spec.or(UserSpecifications.containsSurname(param))
                                .and(UserSpecifications.hasIdIn(clinicUserIds));
                    }
                } else {
                    clinicUserIds = clinicsClient.getUsersByClinicId(clinicId).getBody();
                    for (int i = 1; i < partes.length; i++) {
                        String apellido = String.join(" ", Arrays.copyOfRange(partes, 0, i));
                        String nombre = String.join(" ", Arrays.copyOfRange(partes, i, partes.length));


                        spec = spec.or(UserSpecifications.containsName(nombre)
                                .and(UserSpecifications.containsSurname(apellido))
                                .and(UserSpecifications.hasIdIn(clinicUserIds)));
                    }
                }
            }
        }


        // Devolver los resultados paginados
        return userRepository.findAll(spec, pageable)
                .map(user -> {
                    UserAnimalsResponse response = userMapper.userToUserAnimalsResponse(user);
                    response.setAnimals(animalsClient.getUserAnimals(user.getId()).getBody());
                    return response;
                });
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

    @Transactional
    @Override
    public UserResponse createUserByVet(ClientRegister userRegister, JwtUserDTO userDTO) {

        VetProfile vetProfile = vetProfileRepository.findByUserId(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        System.out.println(userDTO);
        if (!userDTO.getRoles().contains("ROLE_VET")) {

            throw new UnauthorizedException("The user is not authorized!");

        }

        System.out.println("paso");
        User user = userMapper.clientRegisterToUser(userRegister);
        Optional<Role> role = roleRepository.findByName("ROLE_USER");
        role.ifPresent(value -> user.getRoles().add(value));

        user.setEnabled(true);

        if (user.getAddresses() == null) {
            user.setAddresses(new HashSet<>());
        }

        if (userRegister.getAddress() != null && userRegister.getAddress().getAddressLine1() != null) {
            Address address = addressMapper.addressDtoToAddress(userRegister.getAddress());

            address.setUser(user);
            user.getAddresses().add(address);

        }

        userRepository.save(user);

        ClinicClientDTO clinicClient = ClinicClientDTO.builder()
                .clinicId(userDTO.getClinicId())
                .userId(user.getId())
                .accountBalance(0.0)
                .build();

        clinicsClient.addClinicClient(clinicClient);

        return userMapper.userToUserResponse(user);
    }

    @Override
    public List<UserResponse> createUsersByVetMigrator(List<ClientRegister> clients, Long vetClinicId) {

        List<UserResponse> response = new ArrayList<>();

        clients.forEach(clientRegister -> {

            User user = userMapper.clientRegisterToUser(clientRegister);
            Optional<Role> role = roleRepository.findByName("ROLE_USER");
            role.ifPresent(value -> user.getRoles().add(value));

            user.setEnabled(true);

            if (user.getAddresses() == null) {
                user.setAddresses(new HashSet<>());
            }


            if (clientRegister.getAddress() != null && clientRegister.getAddress().getAddressLine1() != null) {

                Address address = addressMapper.addressDtoToAddress(clientRegister.getAddress());

                address.setUser(user);
                user.getAddresses().add(address);
            }

            user.setCodigoVetter(vetClinicId + "-" + clientRegister.getCodigo());


            userRepository.save(user);

            ClinicClientDTO clinicClient = ClinicClientDTO.builder()
                    .clinicId(vetClinicId)
                    .userId(user.getId())
                    .accountBalance(0.0)
                    .build();

            clinicsClient.addClinicClient(clinicClient);

            response.add(userMapper.userToUserResponse(user));
        });

        return response;
    }

    @Override
    public HashMap<String, UserResponse> createUsersByCpVetMigrator(HashMap<String, ClientRegister> users, Long vetClinicId) {
        HashMap<String, UserResponse> response = new HashMap<>();

        users.keySet().forEach(client -> {

            ClientRegister clientRegister = users.get(client);
            User user = userMapper.clientRegisterToUser(clientRegister);
            Optional<Role> role = roleRepository.findByName("ROLE_USER");
            role.ifPresent(value -> user.getRoles().add(value));

            user.setEnabled(true);

            if (user.getAddresses() == null) {
                user.setAddresses(new HashSet<>());
            }

            if (clientRegister.getAddress() != null && clientRegister.getAddress().getAddressLine1() != null) {

                Address address = addressMapper.addressDtoToAddress(clientRegister.getAddress());

                address.setUser(user);
                user.getAddresses().add(address);
            }

            user.setCodigoVetter(vetClinicId + "-" + clientRegister.getCodigo());

            userRepository.save(user);

            ClinicClientDTO clinicClient = ClinicClientDTO.builder()
                    .clinicId(vetClinicId)
                    .userId(user.getId())
                    .accountBalance(0.0)
                    .build();

            clinicsClient.addClinicClient(clinicClient);

            response.put(client, userMapper.userToUserResponse(user));
        });

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
    public UserResponse updateUser(UserDTO userDTO, Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        userMapper.updateUser(userDTO, user);

        // Clear the existing addresses without replacing the collection
        user.getAddresses().clear();

        if (userDTO.getAddresses() != null) {
            Set<Address> addresses = userDTO.getAddresses().stream().map(addressDTO -> {
                Address address = addressMapper.addressDtoToAddress(addressDTO);
                address.setUser(user);
                return address;
            }).collect(Collectors.toSet());

            user.getAddresses().addAll(addresses); // Add new addresses
        }

        // Save the user with updated addresses
        return userMapper.userToUserResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        userRepository.delete(user);

    }

    @Override
    public String sendInviteMail(String email, JwtUserDTO userDTO) {

        if (!userDTO.getRoles().contains("ROLE_VET")){
            throw new UnauthorizedException("The user is not authorized!");
        }

        VetProfile vetProfile = vetProfileRepository.findByUserId(userDTO.getId()).orElseThrow(()-> new RuntimeException("Resource not found"));
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new RuntimeException("Resource not found"));

        ResponseEntity<List<AnimalCompleteResponse>> animals = animalsClient.getUserAnimals(user.getId());
        String name = "";

        if (animals.hasBody() && !animals.getBody().isEmpty()){
             name = animals.getBody().get(0).getName();
        }


        try {
            ClassPathResource resource = new ClassPathResource("templates/emailContent.html");
            StringBuilder content = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

            }


            String htmlContent = content.toString()
                    .replace("$veterinarioname", vetProfile.getUser().getName())
                    .replace("$petname1", name)
                    .replace("$username", user.getName())
                    .replace("$user_email", user.getEmail())
                    .replace("$random", "Liro2024");




            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");


            helper.setFrom("quinterosjuanmanuel1@gmail.com");
            helper.setTo(user.getEmail());
            helper.setSubject("¡INVITACIÓN A LIRO!");
            helper.setText(htmlContent, true);

//            helper.addInline("headerImage", new ClassPathResource("images/header-02.webp"));
//            helper.addInline("downloadButton", new ClassPathResource("images/descargar_btn.webp"));
//            helper.addInline("miniLogo", new ClassPathResource("images/mini_loog.webp"));

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "EXITO";
    }
}




