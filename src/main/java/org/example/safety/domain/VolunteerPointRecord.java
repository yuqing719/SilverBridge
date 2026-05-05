package org.example.safety.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "volunteer_points")
public class VolunteerPointRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User volunteer;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private ServiceOrder serviceOrder;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false, length = 128)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected VolunteerPointRecord() {
    }

    public VolunteerPointRecord(User volunteer, ServiceOrder serviceOrder, Integer points, String description, LocalDateTime createdAt) {
        this.volunteer = volunteer;
        this.serviceOrder = serviceOrder;
        this.points = points;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public User getVolunteer() {
        return volunteer;
    }

    public ServiceOrder getServiceOrder() {
        return serviceOrder;
    }

    public Integer getPoints() {
        return points;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
