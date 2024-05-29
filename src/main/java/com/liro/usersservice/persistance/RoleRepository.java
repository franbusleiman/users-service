package com.liro.usersservice.persistance;

import com.liro.usersservice.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Id;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Id> {

    Optional<Role> findByName(String name);
}
