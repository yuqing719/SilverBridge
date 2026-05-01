package org.example.safety.repo;

import org.example.safety.domain.ActivityCheckin;
import org.example.safety.domain.User;
import org.example.safety.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityCheckinRepository extends JpaRepository<ActivityCheckin, Long> {
    Optional<ActivityCheckin> findByUserAndActivity(User user, Activity activity);
    boolean existsByUserAndActivity(User user, Activity activity);
}
