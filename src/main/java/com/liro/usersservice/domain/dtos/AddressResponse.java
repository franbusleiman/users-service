package com.liro.usersservice.domain.dtos;

import com.liro.usersservice.domain.validationGroups.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class AddressResponse {

    private Long id;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String location;
    private String country;
    private String postalCode;
    private Boolean mainAddress;
}
