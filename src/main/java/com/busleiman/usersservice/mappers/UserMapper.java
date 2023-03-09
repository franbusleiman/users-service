package com.busleiman.usersservice.mappers;

import com.busleiman.usersservice.domain.User;
import com.busleiman.usersservice.domain.dtos.UserDTO;
import com.busleiman.usersservice.domain.dtos.UserRegister;
import com.busleiman.usersservice.domain.dtos.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

     UserMapper userMapper = Mappers.getMapper(UserMapper.class);


    @Mapping(target = "id", ignore = true)
    User userDTOToUser(UserDTO userDTO);

    User userRegisterToUser(UserRegister userRegister);

    UserResponse userToUserResponse(User user);
}
