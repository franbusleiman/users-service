package com.busleiman.usersservice.domain.dtos;

import com.busleiman.usersservice.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class UserResponse  extends UserDTO{

    private Long id;
    private List<Role> roles;
}
