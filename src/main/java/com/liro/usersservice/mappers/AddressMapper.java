package com.liro.usersservice.mappers;

import com.liro.usersservice.domain.dtos.AddressDTO;
import com.liro.usersservice.domain.dtos.AddressResponse;
import com.liro.usersservice.domain.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponse addressToAddressResponse(Address address);

    @Mapping(target = "user", ignore = true)
    Address addressDtoToAddress(AddressDTO addressDTO);
}
