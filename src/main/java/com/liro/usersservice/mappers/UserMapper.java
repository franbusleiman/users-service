package com.liro.usersservice.mappers;

import com.liro.usersservice.domain.dtos.users.*;
import com.liro.usersservice.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(uses = AddressMapper.class)
public interface UserMapper {

     UserMapper userMapper = Mappers.getMapper(UserMapper.class);


    @Mapping(target = "id", ignore = true)
    User userDTOToUser(UserDTO userDTO);

    User userRegisterToUser(UserRegister userRegister);

    @Mapping(source = "addresses", target = "addresses")
    UserResponse userToUserResponse(User user);

    UserCompleteResponse userToUseCompleteResponse(User user);

    UserAnimalsResponse userToUserAnimalsResponse(User user);

    User clientRegisterToUser(ClientRegister clientRegister);

    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "email", ignore = true)
    void updateUser(UserDTO userDTO, @MappingTarget User user);
}
