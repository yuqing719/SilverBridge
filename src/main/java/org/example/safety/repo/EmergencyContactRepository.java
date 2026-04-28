package org.example.safety.repo;

import org.example.safety.domain.EmergencyContact;
import org.example.safety.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {
    List<EmergencyContact> findByElderOrderBySortOrderAsc(User elder);
}
