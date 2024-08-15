package com.liro.usersservice.configuration;


import com.liro.usersservice.domain.dtos.animals.AnimalCompleteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "animals-service")
public interface FeignAnimalsClient {
    @RequestMapping(method = RequestMethod.GET, value = "/animals/user/{userId}")
    ResponseEntity<List<AnimalCompleteResponse>> getUserAnimals(@PathVariable("userId") Long userId);
}