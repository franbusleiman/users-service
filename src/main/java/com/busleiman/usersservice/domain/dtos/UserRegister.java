package com.busleiman.usersservice.domain.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class UserRegister {

    @NotBlank(message = "username must not be null")
    private String username;

    @NotBlank(message = "name must not be null")

    private String name;
    private String lastName;
    @Email(message = "must be an valid email")
    private String email;
    private Long latitude;
    private Long longitude;
    private String password;
}
