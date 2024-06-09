package com.liro.usersservice.mappers;

import com.liro.usersservice.domain.dtos.address.AddressDTO;
import com.liro.usersservice.domain.dtos.address.AddressResponse;
import com.liro.usersservice.domain.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponse addressToAddressResponse(Address address);

    @Mapping(target = "user", ignore = true)
    Address addressDtoToAddress(AddressDTO addressDTO);

    Set<AddressDTO> addressesToAddressDTOs(Set<Address> addresses);
}
