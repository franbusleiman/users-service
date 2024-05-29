package com.liro.usersservice.persistance;

import com.liro.usersservice.domain.model.Address;
import com.liro.usersservice.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
