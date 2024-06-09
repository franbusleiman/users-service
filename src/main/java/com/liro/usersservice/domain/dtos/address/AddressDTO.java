package com.liro.usersservice.domain.dtos.address;


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
public class AddressDTO {


    @NotBlank(groups = {ValidationGroups.Create.class}, message = "Address Line cannot  be blank")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(groups = {ValidationGroups.Create.class}, message = "City cannot be blank")
    private String city;

    private String location;

    @NotBlank(groups = {ValidationGroups.Create.class}, message = "Country cannot be blank")
    private String country;

    private String postalCode;
}
