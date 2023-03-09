package com.busleiman.usersservice.domain.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class UserRegister {

    private String username;
    private String name;
    private String lastName;
    private String email;
    private Long latitude;
    private Long longitude;
    private String password;
}
