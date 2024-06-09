package com.liro.usersservice.services;

import com.liro.usersservice.domain.dtos.address.AddressDTO;
import com.liro.usersservice.domain.dtos.address.AddressResponse;

public interface AddressService {

    AddressResponse createAddress(AddressDTO addressDTO, Long userId);

}
