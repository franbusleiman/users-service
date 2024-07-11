package com.liro.usersservice.services;

import com.liro.usersservice.domain.dtos.users.VetProfileDTO;
import com.liro.usersservice.domain.dtos.users.VetProfileResponse;
import com.liro.usersservice.domain.model.Role;
import com.liro.usersservice.domain.model.User;
import com.liro.usersservice.domain.model.VetProfile;
import com.liro.usersservice.exceptions.BadRequestException;
import com.liro.usersservice.exceptions.ResourceNotFoundException;
import com.liro.usersservice.mappers.VetProfileMapper;
import com.liro.usersservice.persistance.RoleRepository;
import com.liro.usersservice.persistance.UserRepository;
import com.liro.usersservice.persistance.VetProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.liro.usersservice.services.Util.updateIfNotNull;


@Service
public class VetProfileServiceImpl implements VetProfileService {

    private final UserRepository userRepository;
    private final VetProfileRepository vetProfileRepository;
    private final RoleRepository roleRepository;
    private final VetProfileMapper vetProfileMapper;

    @Autowired
    public VetProfileServiceImpl(UserRepository userRepository,
                                 VetProfileRepository vetProfileRepository,
                                 VetProfileMapper vetProfileMapper,
                                 RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.vetProfileRepository = vetProfileRepository;
        this.vetProfileMapper = vetProfileMapper;
    }

    @Override
    public VetProfileResponse createVetProfile(VetProfileDTO vetProfileDTO, Long userId) {

        // TODO: Validate plate

        VetProfile vetProfile = vetProfileMapper.vetProfileDtoToVetProfile(vetProfileDTO);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        if (user.getVetProfile() != null) {
            throw new BadRequestException("User with id: " + userId + " already have a vetProfile");
        }

        Optional<Role> role = roleRepository.findByName("ROLE_VET");

        role.ifPresent(value -> user.getRoles().add(value));

        user.setEnabled(true);

        return vetProfileMapper.vetProfileToVetProfileResponse(vetProfileRepository.save(vetProfile));
    }

    @Override
    public VetProfileResponse getVetProfileResponse(Long userId) {
        VetProfile vetProfile = vetProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("VetProfile not found with userId: " + userId));
        return vetProfileMapper.vetProfileToVetProfileResponse(vetProfile);
    }

    @Override
    public void updateVetProfile(VetProfileDTO vetProfileDTO, Long userId) {
        VetProfile vetProfile = vetProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("VetProfile not found with userId: " + userId));

        if (vetProfileDTO.getPlate() != null) {
            vetProfile.setPlate(vetProfileDTO.getPlate());
            vetProfile.setEnabled(false);
        }

        updateIfNotNull(vetProfile::setEnabled, vetProfileDTO.isEnabled());

        vetProfileRepository.save(vetProfile);
    }

    @Override
    public void changeEnabledState(Boolean enabled, Long userId) {

        VetProfile vetProfile = vetProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("VetProfile not found with userId: " + userId));

        vetProfile.setEnabled(enabled);

        vetProfileRepository.save(vetProfile);
    }
}
