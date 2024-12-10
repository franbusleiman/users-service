package com.liro.usersservice.domain.dtos.users;

import com.liro.usersservice.domain.dtos.address.AddressDTO;
import com.liro.usersservice.domain.enums.Gender;
import com.liro.usersservice.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class UserCompleteResponse {

    private Long id;
    private List<Role> roles;
    private String username;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private String areaPhoneNumber;
    private String identificationNr;
    private String password;
    private LocalDate birthDate;
    private boolean isEnabled;
    private Integer intents;
    private Gender gender;

    private Set<AddressDTO> addresses;
}
