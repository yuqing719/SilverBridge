package org.example.safety;

import org.example.safety.domain.ServiceOrder;
import org.example.safety.domain.ServiceOrderStatus;
import org.example.safety.service.OrderAlreadyAcceptedException;
import org.example.safety.service.VolunteerOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class VolunteerOrderServiceTest {

    @Autowired
    VolunteerOrderService volunteerOrderService;

    @Test
    void elder_can_create_order_with_pending_status() {
        ServiceOrder order = volunteerOrderService.createOrder(1L, 1L, "今天下午需要陪同买菜");

        assertThat(order.getId()).isNotNull();
        assertThat(order.getStatus()).isEqualTo(ServiceOrderStatus.PENDING);
        assertThat(order.getVolunteer()).isNull();
        assertThat(order.getElder().getId()).isEqualTo(1L);
    }

    @Test
    void volunteer_can_accept_order_only_once() {
        ServiceOrder created = volunteerOrderService.createOrder(1L, 1L, "需要帮助");

        ServiceOrder accepted = volunteerOrderService.acceptOrder(created.getId(), 3L);
        assertThat(accepted.getStatus()).isEqualTo(ServiceOrderStatus.ACCEPTED);
        assertThat(accepted.getVolunteer().getId()).isEqualTo(3L);

        assertThrows(OrderAlreadyAcceptedException.class,
                () -> volunteerOrderService.acceptOrder(created.getId(), 3L));
    }

    @Test
    void completing_service_should_settle_points() {
        ServiceOrder created = volunteerOrderService.createOrder(1L, 2L, "请上门测血压");
        volunteerOrderService.acceptOrder(created.getId(), 3L);
        ServiceOrder completed = volunteerOrderService.completeOrder(created.getId(), 3L);

        assertThat(completed.getStatus()).isEqualTo(ServiceOrderStatus.COMPLETED);

        VolunteerOrderService.VolunteerPointsSummary summary = volunteerOrderService.getVolunteerPoints(3L);
        assertThat(summary.totalPoints()).isEqualTo(12);
        assertThat(summary.records()).hasSize(1);
        assertThat(summary.records().get(0).getServiceOrder().getId()).isEqualTo(created.getId());
    }
}
