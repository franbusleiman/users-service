package com.liro.usersservice.services;

import com.liro.usersservice.domain.dtos.usersPreLunch.UserPreLunchRegister;
import com.liro.usersservice.domain.dtos.usersPreLunch.UserPreLunchResponse;
import com.liro.usersservice.domain.model.UserPreLunch;
import com.liro.usersservice.mappers.UserPreLunchMapper;
import com.liro.usersservice.persistance.UserPreLunchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPreLunchServiceImpl implements UserPreLunchService{

    @Autowired(required=true)
    UserPreLunch userPreLunch;

    @Autowired
    UserPreLunchRepository userPreLunchRepository;

    @Autowired
    UserPreLunchMapper userPreLunchMapper;
    ;


    @Override
    public UserPreLunchResponse createUserPreLunch(UserPreLunchRegister userPreLunchRegister) {

        UserPreLunch userPreLunch1 = userPreLunchMapper.userPreLunchRegisterToUserPreLunch(userPreLunchRegister);

            return userPreLunchMapper.userPreLunchToUserPreLunchResponse(userPreLunchRepository.save(userPreLunch1));
    }

}
