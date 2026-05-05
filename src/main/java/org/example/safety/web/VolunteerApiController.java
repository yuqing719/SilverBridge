package org.example.safety.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.example.safety.domain.ServiceOrder;
import org.example.safety.domain.User;
import org.example.safety.domain.UserRole;
import org.example.safety.service.UnauthorizedException;
import org.example.safety.service.VolunteerOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/volunteer-services")
public class VolunteerApiController {
    private final VolunteerOrderService volunteerOrderService;

    public VolunteerApiController(VolunteerOrderService volunteerOrderService) {
        this.volunteerOrderService = volunteerOrderService;
    }

    @GetMapping
    public List<ServiceResponse> listServices() {
        return volunteerOrderService.listServices().stream()
                .map(s -> new ServiceResponse(s.getId(), s.getName(), s.getDescription(), s.getPoints()))
                .toList();
    }

    @PostMapping("/orders")
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request, HttpSession session) {
        SessionUtil.LoginUser loginUser = requireRole(session, UserRole.ELDER);
        ServiceOrder order = volunteerOrderService.createOrder(loginUser.id(), request.serviceId(), request.remark());
        return toOrderResponse(order);
    }

    @GetMapping("/orders/pending")
    public List<OrderResponse> listPendingOrders(HttpSession session) {
        requireRole(session, UserRole.VOLUNTEER);
        return volunteerOrderService.listPendingOrders().stream().map(this::toOrderResponse).toList();
    }

    @GetMapping("/orders/mine")
    public List<OrderResponse> listMyOrders(HttpSession session) {
        SessionUtil.LoginUser user = requireLogin(session);
        List<ServiceOrder> orders = user.role() == UserRole.VOLUNTEER
                ? volunteerOrderService.listOrdersForVolunteer(user.id())
                : volunteerOrderService.listOrdersForElder(user.id());
        return orders.stream().map(this::toOrderResponse).toList();
    }

    @PostMapping("/orders/{orderId}/accept")
    public OrderResponse acceptOrder(@PathVariable long orderId, HttpSession session) {
        SessionUtil.LoginUser volunteer = requireRole(session, UserRole.VOLUNTEER);
        return toOrderResponse(volunteerOrderService.acceptOrder(orderId, volunteer.id()));
    }

    @PostMapping("/orders/{orderId}/complete")
    public OrderResponse completeOrder(@PathVariable long orderId, HttpSession session) {
        SessionUtil.LoginUser volunteer = requireRole(session, UserRole.VOLUNTEER);
        return toOrderResponse(volunteerOrderService.completeOrder(orderId, volunteer.id()));
    }

    @GetMapping("/points/me")
    public PointsSummaryResponse myPoints(HttpSession session) {
        SessionUtil.LoginUser volunteer = requireRole(session, UserRole.VOLUNTEER);
        VolunteerOrderService.VolunteerPointsSummary summary = volunteerOrderService.getVolunteerPoints(volunteer.id());
        return new PointsSummaryResponse(
                summary.totalPoints(),
                summary.records().stream().map(r -> new PointRecordResponse(
                        r.getId(),
                        r.getServiceOrder().getId(),
                        r.getDescription(),
                        r.getPoints(),
                        r.getCreatedAt()
                )).toList()
        );
    }

    private SessionUtil.LoginUser requireLogin(HttpSession session) {
        SessionUtil.LoginUser user = SessionUtil.get(session);
        if (user == null) {
            throw new UnauthorizedException("请先登录");
        }
        return user;
    }

    private SessionUtil.LoginUser requireRole(HttpSession session, UserRole role) {
        SessionUtil.LoginUser user = requireLogin(session);
        if (user.role() != role) {
            throw new UnauthorizedException("无权访问该功能");
        }
        return user;
    }

    private OrderResponse toOrderResponse(ServiceOrder order) {
        User volunteer = order.getVolunteer();
        return new OrderResponse(
                order.getId(),
                order.getService().getId(),
                order.getService().getName(),
                order.getService().getDescription(),
                order.getService().getPoints(),
                order.getElder().getId(),
                order.getElder().getName(),
                volunteer == null ? null : volunteer.getId(),
                volunteer == null ? null : volunteer.getName(),
                order.getStatus().name(),
                order.getRemark(),
                order.getCreatedAt(),
                order.getAcceptedAt(),
                order.getCompletedAt()
        );
    }

    public record CreateOrderRequest(long serviceId,
                                     @Size(max = 255, message = "备注最多 255 个字")
                                     String remark) {
    }

    public record ServiceResponse(long id, String name, String description, int points) {
    }

    public record OrderResponse(long id,
                                long serviceId,
                                String serviceName,
                                String serviceDescription,
                                int points,
                                long elderId,
                                String elderName,
                                Long volunteerId,
                                String volunteerName,
                                String status,
                                String remark,
                                LocalDateTime createdAt,
                                LocalDateTime acceptedAt,
                                LocalDateTime completedAt) {
    }

    public record PointRecordResponse(long id,
                                      long orderId,
                                      String description,
                                      int points,
                                      LocalDateTime createdAt) {
    }

    public record PointsSummaryResponse(int totalPoints, List<PointRecordResponse> records) {
    }
}
