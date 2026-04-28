package org.example.safety.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "checkins",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_checkin_elder_date", columnNames = {"elder_id", "checkin_date"})
        }
)
public class Checkin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "elder_id", nullable = false)
    private User elder;

    @Column(name = "checkin_date", nullable = false)
    private LocalDate checkinDate;

    @Column(name = "checkin_time", nullable = false)
    private LocalDateTime checkinTime;

    protected Checkin() {
    }

    public Checkin(User elder, LocalDate checkinDate, LocalDateTime checkinTime) {
        this.elder = elder;
        this.checkinDate = checkinDate;
        this.checkinTime = checkinTime;
    }

    public Long getId() {
        return id;
    }

    public User getElder() {
        return elder;
    }

    public LocalDate getCheckinDate() {
        return checkinDate;
    }

    public LocalDateTime getCheckinTime() {
        return checkinTime;
    }
}
