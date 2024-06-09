package com.liro.usersservice.domain.dtos.users;

import com.liro.usersservice.domain.dtos.address.AddressDTO;
import com.liro.usersservice.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class UserResponse  extends UserDTO {

    private Long id;
    private List<Role> roles;
    private Set<AddressDTO> addresses;
}
