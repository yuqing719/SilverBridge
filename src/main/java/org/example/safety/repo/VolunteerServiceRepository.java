package org.example.safety.repo;

import org.example.safety.domain.VolunteerService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerServiceRepository extends JpaRepository<VolunteerService, Long> {
}
