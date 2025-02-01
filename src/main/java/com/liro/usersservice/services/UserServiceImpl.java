package com.liro.usersservice.services;

import brave.Tracer;
import com.liro.usersservice.configuration.FeignAnimalsClient;
import com.liro.usersservice.configuration.FeignClinicClientClient;
import com.liro.usersservice.configuration.PasswordGenerator;
import com.liro.usersservice.domain.dtos.users.*;
import com.liro.usersservice.domain.enums.State;
import com.liro.usersservice.domain.model.*;
import com.liro.usersservice.exceptions.ResourceNotFoundException;
import com.liro.usersservice.exceptions.UnauthorizedException;
import com.liro.usersservice.mappers.AddressMapper;
import com.liro.usersservice.mappers.UserMapper;
import com.liro.usersservice.persistance.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.charset.StandardCharsets;

import java.time.LocalDateTime;
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
    private JavaMailSender mailSender;
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
    private UserInviteRepository userInviteRepository;

    UserMapper userMapper = UserMapper.userMapper;


    @Override
    public UserCompleteResponse findById(Long id) {
        return userMapper.userToUseCompleteResponse(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public UserResponse findByIdentificationNr(String id) {
        return userMapper.userToUserResponse(userRepository.findUserByIdentificationNr(id)
                .orElseThrow(() -> new RuntimeException("Resource not found")));
    }

    //TODO: DELETE THIS METHOD
    @Override
    public UserResponse setAccount(SetAccountDTO setAccountDTO) {

        User user = userRepository.findUserByEmail(setAccountDTO.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        if (user.getPassword() == null) {
            user.setPassword(passwordEncoder.encode(setAccountDTO.getPassword()));
        }

        return userMapper.userToUserResponse(userRepository.save(user));
    }


    @Override
    public UserCompleteResponse findByUsername(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by username"));
        return userMapper.userToUseCompleteResponse(user);

    }

    @Override
    public UserCompleteResponse findByEmail(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by email"));
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


        // TODO: REQUEST ALL THE ANIMALS FROM THE IDS AT ONCE
        return userRepository.findAll(spec, pageable)
                .map(user -> {
                    UserAnimalsResponse response = userMapper.userToUserAnimalsResponse(user);
                    response.setAnimals(animalsClient.getUserAnimals(user.getId()).getBody());
                    return response;
                });
    }

    //TODO: CHANGE THIS LEGACY FLOW IF ITS GOING TO BE USED, USER CAN NOT SET NEITHER EMAIL OR PASSWORD.
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

        if (!userDTO.getRoles().contains("ROLE_VET")) {

            throw new UnauthorizedException("The user is not authorized!");

        }

        User user = userMapper.clientRegisterToUser(userRegister);
        Optional<Role> role = roleRepository.findByName("ROLE_USER");
        role.ifPresent(value -> user.getRoles().add(value));

        user.setEnabled(true);
        user.setState(State.LOCAL);

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
            user.setState(State.LOCAL);

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
            user.setState(State.LOCAL);

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

    //TODO: CHECK IF THE USER IS THE ONE WHO IS EDITING, OR IF IT IS LOCAL (AND THE VET IS THE ONE EDITING)
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

    @Transactional
    @Override
    public void sendInviteMail(String email, JwtUserDTO userDTO, Long userId) throws MessagingException, IOException {


        //Validaciones de que es vet y se podrían sacar
        VetProfile vetProfile = vetProfileRepository.findByUserId(userDTO.getId()).orElseThrow(() -> new RuntimeException("Resource not found"));

        if (!userDTO.getRoles().contains("ROLE_VET")) {
            throw new UnauthorizedException("The user is not authorized!");
        }

        //Validar que el mail   no se esta usando
        if (userRepository.findUserByEmail(email).isPresent() || userInviteRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Resource not found"));

        if (user.getState() == State.ACCEPTED) {
            throw new RuntimeException("User already active!");
        }

        String generatedPassword = PasswordGenerator.generatePassword();
        String passwordEncoded = passwordEncoder.encode(generatedPassword);

        //Modificar invite o crear nuevo, dependiendo si el user tiene un invite activo
        UserInvite userInvite = userInviteRepository.findByUserId(userId)
                .map(userInvite1 -> {
                    userInvite1.setCreatedAt(LocalDateTime.now());
                    userInvite1.setEmail(email);
                    userInvite1.setPassword(passwordEncoded);
                    return userInvite1;
                })
                .orElse(UserInvite.builder().createdAt(LocalDateTime.now())
                        .user(user)
                        .email(email)
                        .password(passwordEncoded)
                        .build());

        //Guardar user e invite
        user.setState(State.PENDING);

        userRepository.save(user);
        userInviteRepository.save(userInvite);

        sendEmail(vetProfile, user, email, generatedPassword);
    }

    @Transactional
    @Override
    public void acceptInvite(String email, String password) {
        UserInvite userInvite = userInviteRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Su invitacion no existe o ha expirado"));

        if (!passwordEncoder.matches(password, userInvite.getPassword())) {
            throw new RuntimeException("password incorrecta");
        }

        User user = userInvite.getUser();

        user.setEmail(userInvite.getEmail());
        user.setPassword(passwordEncoder.encode(password));
        user.setState(State.ACCEPTED);

        userRepository.save(user);
        userInviteRepository.delete(userInvite);
    }

    @Override
    public Boolean existInvite(String email) {
        return userInviteRepository.existsByEmail(email);
    }

    @Override
    public void setFirebaseToken(String firebaseToken, JwtUserDTO userDTO) {

       User user =  userRepository.findById(userDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

       user.setFirebaseToken(firebaseToken);

       userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        userRepository.delete(user);

    }

    private void sendEmail(VetProfile vetProfile, User user, String email, String generatedPassword) throws IOException, MessagingException {
        String name = "";

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
                .replace("$user_email", email)
                .replace("$random", generatedPassword);




        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");


        helper.setFrom("quinterosjuanmanuel1@gmail.com");
        helper.setTo(email);
        helper.setSubject("¡INVITACIÓN A LIRO!");
        helper.setText(htmlContent, true);

//            helper.addInline("headerImage", new ClassPathResource("images/header-02.webp"));
//            helper.addInline("downloadButton", new ClassPathResource("images/descargar_btn.webp"));
//            helper.addInline("miniLogo", new ClassPathResource("images/mini_loog.webp"));

        mailSender.send(message);
    }
}
