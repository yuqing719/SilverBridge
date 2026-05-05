package org.example.safety.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_orders")
public class ServiceOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private VolunteerService service;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User elder;

    @ManyToOne(fetch = FetchType.LAZY)
    private User volunteer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ServiceOrderStatus status;

    @Column(nullable = false, length = 255)
    private String remark;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime completedAt;

    @Version
    private Long version;

    protected ServiceOrder() {
    }

    public ServiceOrder(VolunteerService service, User elder, String remark, LocalDateTime createdAt) {
        this.service = service;
        this.elder = elder;
        this.remark = remark;
        this.createdAt = createdAt;
        this.status = ServiceOrderStatus.PENDING;
    }

    public Long getId() {
        return id;
    }

    public VolunteerService getService() {
        return service;
    }

    public User getElder() {
        return elder;
    }

    public User getVolunteer() {
        return volunteer;
    }

    public ServiceOrderStatus getStatus() {
        return status;
    }

    public String getRemark() {
        return remark;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void acceptBy(User volunteer, LocalDateTime acceptedAt) {
        this.volunteer = volunteer;
        this.acceptedAt = acceptedAt;
        this.status = ServiceOrderStatus.ACCEPTED;
    }

    public void complete(LocalDateTime completedAt) {
        this.completedAt = completedAt;
        this.status = ServiceOrderStatus.COMPLETED;
    }
}
