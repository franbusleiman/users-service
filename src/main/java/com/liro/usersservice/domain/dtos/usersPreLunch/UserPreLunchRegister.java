package com.liro.usersservice.domain.dtos.usersPreLunch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class UserPreLunchRegister {

    @NotNull
    private String email;

    @NotNull
    private String name;

}
