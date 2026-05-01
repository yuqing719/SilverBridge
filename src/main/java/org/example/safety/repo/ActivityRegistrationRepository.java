package org.example.safety.repo;

import org.example.safety.domain.ActivityRegistration;
import org.example.safety.domain.User;
import org.example.safety.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityRegistrationRepository extends JpaRepository<ActivityRegistration, Long> {
    List<ActivityRegistration> findByUser(User user);
    Optional<ActivityRegistration> findByUserAndActivity(User user, Activity activity);
    boolean existsByUserAndActivity(User user, Activity activity);
}
