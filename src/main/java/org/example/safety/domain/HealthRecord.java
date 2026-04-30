package org.example.safety.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_records")
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "elder_id", nullable = false)
    private Long elderId;

    @Column(name = "record_time", nullable = false)
    private LocalDateTime recordTime;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "systolic_pressure")
    private Integer systolicPressure;

    @Column(name = "diastolic_pressure")
    private Integer diastolicPressure;

    @Column(name = "blood_sugar")
    private Double bloodSugar;

    @Column(name = "notes", length = 255)
    private String notes;

    protected HealthRecord() {}

    public HealthRecord(Long elderId, LocalDateTime recordTime, Integer heartRate, Integer systolicPressure, Integer diastolicPressure, Double bloodSugar, String notes) {
        this.elderId = elderId;
        this.recordTime = recordTime;
        this.heartRate = heartRate;
        this.systolicPressure = systolicPressure;
        this.diastolicPressure = diastolicPressure;
        this.bloodSugar = bloodSugar;
        this.notes = notes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getElderId() { return elderId; }
    public void setElderId(Long elderId) { this.elderId = elderId; }

    public LocalDateTime getRecordTime() { return recordTime; }
    public void setRecordTime(LocalDateTime recordTime) { this.recordTime = recordTime; }

    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }

    public Integer getSystolicPressure() { return systolicPressure; }
    public void setSystolicPressure(Integer systolicPressure) { this.systolicPressure = systolicPressure; }

    public Integer getDiastolicPressure() { return diastolicPressure; }
    public void setDiastolicPressure(Integer diastolicPressure) { this.diastolicPressure = diastolicPressure; }

    public Double getBloodSugar() { return bloodSugar; }
    public void setBloodSugar(Double bloodSugar) { this.bloodSugar = bloodSugar; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
