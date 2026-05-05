package org.example.safety.service;

import org.example.safety.domain.ServiceOrder;
import org.example.safety.domain.ServiceOrderStatus;
import org.example.safety.domain.User;
import org.example.safety.domain.UserRole;
import org.example.safety.domain.VolunteerPointRecord;
import org.example.safety.domain.VolunteerService;
import org.example.safety.repo.ServiceOrderRepository;
import org.example.safety.repo.UserRepository;
import org.example.safety.repo.VolunteerPointRecordRepository;
import org.example.safety.repo.VolunteerServiceRepository;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VolunteerOrderService {
    private final VolunteerServiceRepository volunteerServiceRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final VolunteerPointRecordRepository volunteerPointRecordRepository;
    private final UserRepository userRepository;
    private final TimeProvider timeProvider;

    public VolunteerOrderService(VolunteerServiceRepository volunteerServiceRepository,
                                 ServiceOrderRepository serviceOrderRepository,
                                 VolunteerPointRecordRepository volunteerPointRecordRepository,
                                 UserRepository userRepository,
                                 TimeProvider timeProvider) {
        this.volunteerServiceRepository = volunteerServiceRepository;
        this.serviceOrderRepository = serviceOrderRepository;
        this.volunteerPointRecordRepository = volunteerPointRecordRepository;
        this.userRepository = userRepository;
        this.timeProvider = timeProvider;
    }

    @Transactional(readOnly = true)
    public List<VolunteerService> listServices() {
        return volunteerServiceRepository.findAll();
    }

    @Transactional
    public ServiceOrder createOrder(long elderId, long serviceId, String remark) {
        User elder = requireUserWithRole(elderId, UserRole.ELDER, "老人");
        VolunteerService service = volunteerServiceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("服务不存在: " + serviceId));
        String safeRemark = (remark == null || remark.isBlank()) ? "需要志愿者协助" : remark.trim();
        return serviceOrderRepository.save(new ServiceOrder(service, elder, safeRemark, timeProvider.now()));
    }

    @Transactional(readOnly = true)
    public List<ServiceOrder> listPendingOrders() {
        return serviceOrderRepository.findByStatusOrderByCreatedAtAsc(ServiceOrderStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<ServiceOrder> listOrdersForElder(long elderId) {
        User elder = requireUserWithRole(elderId, UserRole.ELDER, "老人");
        return serviceOrderRepository.findByElderOrderByCreatedAtDesc(elder);
    }

    @Transactional(readOnly = true)
    public List<ServiceOrder> listOrdersForVolunteer(long volunteerId) {
        User volunteer = requireUserWithRole(volunteerId, UserRole.VOLUNTEER, "志愿者");
        return serviceOrderRepository.findByVolunteerOrderByCreatedAtDesc(volunteer);
    }

    @Transactional
    public ServiceOrder acceptOrder(long orderId, long volunteerId) {
        User volunteer = requireUserWithRole(volunteerId, UserRole.VOLUNTEER, "志愿者");
        ServiceOrder order = serviceOrderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("订单不存在: " + orderId));
        if (order.getStatus() != ServiceOrderStatus.PENDING) {
            throw new OrderAlreadyAcceptedException("该订单已被接单");
        }
        order.acceptBy(volunteer, timeProvider.now());
        try {
            return serviceOrderRepository.saveAndFlush(order);
        } catch (OptimisticLockingFailureException ex) {
            throw new OrderAlreadyAcceptedException("该订单已被其他志愿者抢先接单");
        }
    }

    @Transactional
    public ServiceOrder completeOrder(long orderId, long volunteerId) {
        User volunteer = requireUserWithRole(volunteerId, UserRole.VOLUNTEER, "志愿者");
        ServiceOrder order = serviceOrderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("订单不存在: " + orderId));
        if (order.getStatus() != ServiceOrderStatus.ACCEPTED) {
            throw new InvalidOrderStateException("只有已接单订单才能完成");
        }
        if (order.getVolunteer() == null || !order.getVolunteer().getId().equals(volunteer.getId())) {
            throw new UnauthorizedException("只能完成自己接的订单");
        }
        order.complete(timeProvider.now());
        serviceOrderRepository.save(order);
        settlePoints(order, volunteer);
        return order;
    }

    @Transactional(readOnly = true)
    public VolunteerPointsSummary getVolunteerPoints(long volunteerId) {
        User volunteer = requireUserWithRole(volunteerId, UserRole.VOLUNTEER, "志愿者");
        List<VolunteerPointRecord> records = volunteerPointRecordRepository.findByVolunteerOrderByCreatedAtDesc(volunteer);
        int total = records.stream().mapToInt(VolunteerPointRecord::getPoints).sum();
        return new VolunteerPointsSummary(total, records);
    }

    private void settlePoints(ServiceOrder order, User volunteer) {
        if (volunteerPointRecordRepository.existsByServiceOrder(order)) {
            return;
        }
        String description = "完成服务：" + order.getService().getName();
        VolunteerPointRecord record = new VolunteerPointRecord(
                volunteer,
                order,
                order.getService().getPoints(),
                description,
                order.getCompletedAt()
        );
        volunteerPointRecordRepository.save(record);
    }

    private User requireUserWithRole(long userId, UserRole role, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(roleName + "不存在: " + userId));
        if (user.getRole() != role) {
            throw new IllegalArgumentException("当前用户不是" + roleName);
        }
        return user;
    }

    public record VolunteerPointsSummary(int totalPoints, List<VolunteerPointRecord> records) {
    }
}
