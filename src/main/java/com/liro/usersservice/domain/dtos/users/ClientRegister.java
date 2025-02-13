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

    private String username;
    @NotBlank(message = "name must not be null")
    private String name;
    @NotBlank(message = "surname must not be null")
    private String surname;
    private String phoneNumber;
    private String areaPhoneNumber;
    private String identificationNr;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    @NotNull(message = "gender must not be null")
    private Gender gender;

    private AddressDTO address;
    private Double saldo;
    private String codigo;

}