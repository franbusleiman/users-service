package com.liro.usersservice.persistance;

import com.liro.usersservice.domain.model.UserInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserInviteRepository extends JpaRepository<UserInvite, Long> {

        Optional<UserInvite> findByUserId(Long userId);
        Optional<UserInvite> findByEmail(String email);

        boolean existsByEmail(String email);

        List<UserInvite> findBycreatedAtBefore(LocalDateTime dateTime);

}
