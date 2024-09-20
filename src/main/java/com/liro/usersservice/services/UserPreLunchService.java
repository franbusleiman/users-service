package com.liro.usersservice.services;

import com.liro.usersservice.domain.dtos.usersPreLunch.UserPreLunchRegister;
import com.liro.usersservice.domain.dtos.usersPreLunch.UserPreLunchResponse;

public interface UserPreLunchService {

    UserPreLunchResponse createUserPreLunch(UserPreLunchRegister userPreLunchRegister);

}
