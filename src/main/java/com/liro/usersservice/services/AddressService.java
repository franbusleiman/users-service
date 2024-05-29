package com.liro.usersservice.services;

import com.liro.usersservice.domain.dtos.AddressDTO;
import com.liro.usersservice.domain.dtos.AddressResponse;
import com.liro.usersservice.domain.dtos.UserRegister;
import com.liro.usersservice.domain.dtos.UserResponse;
import com.liro.usersservice.domain.model.Address;

public interface AddressService {

    AddressResponse createAddress(AddressDTO addressDTO, Long userId);

}
