package com.liro.usersservice.persistance;

import com.liro.usersservice.domain.model.UserPreLunch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreLunchRepository extends JpaRepository<UserPreLunch, Long> {
}
