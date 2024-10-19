package com.liro.usersservice.configuration;

import com.liro.usersservice.domain.dtos.users.ClinicClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "clinics-service")
public interface FeignClinicClientClient {

    @RequestMapping(method = RequestMethod.GET, value = "/clinicClients/{clinicId}")
    ResponseEntity<List<Long>> getUsersByClinicId(@PathVariable("clinicId") Long clinicId);

    @RequestMapping(method = RequestMethod.POST, value = "/clinicClients")
    ResponseEntity<Void> addClinicClient(@RequestBody ClinicClientDTO clinicClientDTO);
}
