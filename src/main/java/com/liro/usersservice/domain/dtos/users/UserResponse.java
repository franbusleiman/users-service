package com.liro.usersservice.domain.dtos.users;

import com.liro.usersservice.domain.dtos.address.AddressDTO;
import com.liro.usersservice.domain.enums.State;
import com.liro.usersservice.domain.model.Role;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public class UserResponse  extends UserDTO {

    private Long id;
    private State state;

    private List<Role> roles;
    private Set<AddressDTO> addresses;

    private String codigoVetter;
}
