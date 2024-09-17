package com.liro.usersservice.mappers;

import com.liro.usersservice.domain.dtos.usersPreLunch.UserPreLunchRegister;
import com.liro.usersservice.domain.dtos.usersPreLunch.UserPreLunchResponse;
import com.liro.usersservice.domain.model.UserPreLunch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserPreLunchMapper {

    @Mapping(target = "id", ignore = true)
    UserPreLunch userPreLunchRegisterToUserPreLunch(UserPreLunchRegister userPreLunchRegister);

    UserPreLunchResponse userPreLunchToUserPreLunchResponse(UserPreLunch userPreLunch);


}
