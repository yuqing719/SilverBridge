package org.example.safety.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "alerts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_alert_elder_date_type", columnNames = {"elder_id", "alert_date", "type"})
        }
)
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "elder_id", nullable = false)
    private User elder;

    @Column(name = "alert_date", nullable = false)
    private LocalDate alertDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AlertType type;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    protected Alert() {
    }

    public Alert(User elder, LocalDate alertDate, AlertType type, LocalDateTime createdAt) {
        this.elder = elder;
        this.alertDate = alertDate;
        this.type = type;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public User getElder() {
        return elder;
    }

    public LocalDate getAlertDate() {
        return alertDate;
    }

    public AlertType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public boolean isActive() {
        return resolvedAt == null;
    }

    public void resolve(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}
