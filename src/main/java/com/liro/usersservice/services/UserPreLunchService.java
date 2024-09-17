package com.liro.usersservice.services;

import com.liro.usersservice.domain.dtos.usersPreLunch.UserPreLunchRegister;
import com.liro.usersservice.domain.dtos.usersPreLunch.UserPreLunchResponse;

public interface UserPreLunchService {

    public UserPreLunchResponse createUserPreLunch(UserPreLunchRegister userPreLunchRegister);

}
