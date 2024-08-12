package com.liro.usersservice.persistance;

import com.liro.usersservice.domain.model.VetClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VetClientRepository extends JpaRepository<VetClient, Long> {
}
