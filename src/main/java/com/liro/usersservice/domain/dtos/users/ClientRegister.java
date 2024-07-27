package com.liro.usersservice.domain.dtos.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.liro.usersservice.domain.dtos.address.AddressDTO;
import com.liro.usersservice.domain.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class ClientRegister {

    @NotBlank(message = "username must not be null")
    private String username;
    @NotBlank(message = "name must not be null")
    private String name;
    @NotBlank(message = "surname must not be null")
    private String surname;
    @NotBlank(message = "email must not be null")
    @Email(message = "Must be a valid email")
    private String email;
    private String phoneNumber;
    private String areaPhoneNumber;
    @NotBlank(message = "identificationNr must not be null")
    private String identificationNr;
    @NotNull(message = "birthDate must not be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    @NotNull(message = "gender must not be null")
    private Gender gender;

    private AddressDTO address;
}