package com.liro.usersservice.persistance;

import com.liro.usersservice.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByIdentificationNr(String identificationNr);

    Optional<User> findUserByEmail(String username);
    boolean existsByIdentificationNr(String identificationNr);
    boolean existsByEmail(String email);


}
