package com.liro.usersservice.mappers;

import com.liro.usersservice.domain.dtos.users.VetProfileDTO;
import com.liro.usersservice.domain.dtos.users.VetProfileResponse;
import com.liro.usersservice.domain.model.VetProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VetProfileMapper {
    
    VetProfileResponse vetProfileToVetProfileResponse(VetProfile vetProfile);

    @Mapping(target = "user", ignore = true)
    VetProfile vetProfileDtoToVetProfile(VetProfileDTO vetProfileDTO);
}
