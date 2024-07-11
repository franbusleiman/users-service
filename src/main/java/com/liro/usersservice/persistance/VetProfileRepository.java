package com.liro.usersservice.persistance;


import com.liro.usersservice.domain.model.VetProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VetProfileRepository extends JpaRepository<VetProfile, Long> {
    Optional<VetProfile> findByUserId(Long userId);
}