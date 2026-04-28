package org.example.safety.repo;

import org.example.safety.domain.Checkin;
import org.example.safety.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CheckinRepository extends JpaRepository<Checkin, Long> {
    Optional<Checkin> findByElderAndCheckinDate(User elder, LocalDate checkinDate);
    boolean existsByElderAndCheckinDate(User elder, LocalDate checkinDate);
}
