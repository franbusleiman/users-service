package com.liro.usersservice.domain.dtos.users;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public class JwtUserDTO {

    private Long id;
    private String email;
    private List<String> roles;
    private Long clinicId;
}
