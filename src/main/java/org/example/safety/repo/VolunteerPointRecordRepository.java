package org.example.safety.repo;

import org.example.safety.domain.ServiceOrder;
import org.example.safety.domain.User;
import org.example.safety.domain.VolunteerPointRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolunteerPointRecordRepository extends JpaRepository<VolunteerPointRecord, Long> {
    boolean existsByServiceOrder(ServiceOrder serviceOrder);
    List<VolunteerPointRecord> findByVolunteerOrderByCreatedAtDesc(User volunteer);
}
