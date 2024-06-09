package com.liro.usersservice.services;

import com.liro.usersservice.domain.dtos.address.AddressDTO;
import com.liro.usersservice.domain.dtos.address.AddressResponse;
import com.liro.usersservice.domain.model.Address;
import com.liro.usersservice.domain.model.User;
import com.liro.usersservice.exceptions.ResourceNotFoundException;
import com.liro.usersservice.mappers.AddressMapper;
import com.liro.usersservice.persistance.AddressRepository;
import com.liro.usersservice.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class AddressServiceImpl implements AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Autowired
    public AddressServiceImpl(UserRepository userRepository,
                              AddressRepository addressRepository,
                              AddressMapper addressMapper) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }


    @Override
    public AddressResponse createAddress(AddressDTO addressDTO, Long userId) {
        Address address = addressMapper.addressDtoToAddress(addressDTO);

        User user = userRepository
                .findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getAddresses() == null) {
            user.setAddresses(new HashSet<>());
        }

        address.setUser(user);
        user.getAddresses().add(address);

        return addressMapper.addressToAddressResponse(addressRepository.save(address));
    }
}
