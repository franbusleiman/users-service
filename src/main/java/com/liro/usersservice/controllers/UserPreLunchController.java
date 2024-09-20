package com.liro.usersservice.controllers;

import com.liro.usersservice.domain.dtos.usersPreLunch.UserPreLunchRegister;
import com.liro.usersservice.domain.dtos.usersPreLunch.UserPreLunchResponse;
import com.liro.usersservice.mappers.UserPreLunchMapper;
import com.liro.usersservice.services.UserPreLunchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/prelunch")
@RestController
public class UserPreLunchController {

    @Autowired
    UserPreLunchService userPreLunchService;

    @Autowired
    UserPreLunchMapper userPreLunchMapper;

    @PostMapping(value = "/create",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserPreLunchResponse> createUserPreLunch(@RequestBody @Valid UserPreLunchRegister userPreLunchRegister){
        return ResponseEntity.ok().body(userPreLunchService.createUserPreLunch(userPreLunchRegister));
    }

}
