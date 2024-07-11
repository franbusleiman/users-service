package com.liro.usersservice.services;


import com.liro.usersservice.domain.dtos.users.VetProfileDTO;
import com.liro.usersservice.domain.dtos.users.VetProfileResponse;

public interface VetProfileService {

    VetProfileResponse createVetProfile(VetProfileDTO vetProfileDTO, Long userId);

    VetProfileResponse getVetProfileResponse(Long userId);

    void updateVetProfile(VetProfileDTO vetProfileDTO, Long userId);

    void changeEnabledState(Boolean enabled, Long userId);
}
