package com.liro.usersservice.domain.dtos.users;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class ClinicClientDTO {

    private Long userId;
    private Long clinicId;
    private Double accountBalance;
}
