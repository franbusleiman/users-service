package com.busleiman.usersservice.persistance;

import com.busleiman.usersservice.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Id;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Id> {

    Optional<Role> findByName(String name);
}
