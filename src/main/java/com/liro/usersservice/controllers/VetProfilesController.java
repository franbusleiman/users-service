package com.liro.usersservice.controllers;

import com.liro.usersservice.domain.dtos.users.VetProfileDTO;
import com.liro.usersservice.domain.dtos.users.VetProfileResponse;
import com.liro.usersservice.services.VetProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

import static com.liro.usersservice.services.Util.getUserId;


@RestController
@RequestMapping("/vetProfiles")
public class VetProfilesController {

    private final VetProfileService vetProfileService;

    @Autowired
    public VetProfilesController(VetProfileService vetProfileService) {
        this.vetProfileService = vetProfileService;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<VetProfileResponse> getVetProfile(@RequestHeader(name = "Authorization", required = false) String token) {

        return ResponseEntity.ok(vetProfileService
                .getVetProfileResponse(getUserId(token)));
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<VetProfileResponse> createVetProfile(@Valid @RequestBody VetProfileDTO vetProfileDTO,
             @RequestHeader(name = "Authorization", required = false) String token) {

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/vetProfiles")
                .buildAndExpand().toUri();

        return ResponseEntity.created(location).body(vetProfileService
                .createVetProfile(vetProfileDTO, getUserId(token)));
    }

    @PutMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> updateVetProfile(@Valid @RequestBody VetProfileDTO vetProfileDTO,
             @RequestHeader(name = "Authorization", required = false) String token) {
        vetProfileService.updateVetProfile(vetProfileDTO, getUserId(token));

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/changeEnabledState", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> changeEnabledState(@RequestParam("enabled") Boolean enabled,
             @RequestHeader(name = "Authorization", required = false) String token) {

        vetProfileService.changeEnabledState(enabled, getUserId(token));

        return ResponseEntity.ok().build();
    }
}