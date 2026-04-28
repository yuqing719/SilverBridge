package org.example.safety.repo;

import org.example.safety.domain.Alert;
import org.example.safety.domain.AlertType;
import org.example.safety.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    Optional<Alert> findByElderAndAlertDateAndType(User elder, LocalDate alertDate, AlertType type);

    List<Alert> findByAlertDateAndTypeAndResolvedAtIsNull(LocalDate alertDate, AlertType type);

    List<Alert> findByElderAndAlertDateAndResolvedAtIsNull(User elder, LocalDate alertDate);
}
