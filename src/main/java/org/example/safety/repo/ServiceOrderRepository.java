package org.example.safety.repo;

import org.example.safety.domain.ServiceOrder;
import org.example.safety.domain.ServiceOrderStatus;
import org.example.safety.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    List<ServiceOrder> findByStatusOrderByCreatedAtAsc(ServiceOrderStatus status);
    List<ServiceOrder> findByElderOrderByCreatedAtDesc(User elder);
    List<ServiceOrder> findByVolunteerOrderByCreatedAtDesc(User volunteer);
}
