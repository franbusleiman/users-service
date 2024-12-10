package com.liro.usersservice.domain.dtos.users;


import com.liro.usersservice.domain.dtos.address.AddressDTO;
import com.liro.usersservice.domain.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class UserDTO {

    private String username;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private String areaPhoneNumber;
    private String identificationNr;
    private LocalDate birthDate;
    private Integer intents;
    private Gender gender;
    private Set<AddressDTO> addresses;
}
