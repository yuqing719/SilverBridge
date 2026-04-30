package org.example.safety.web;

import org.example.safety.domain.HealthRecord;
import org.example.safety.repo.HealthRecordRepository;
import org.example.safety.service.NotFoundException;
import org.example.safety.domain.User;
import org.example.safety.repo.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/health")
public class HealthApiController {

    private final HealthRecordRepository healthRecordRepository;
    private final UserRepository userRepository;

    public HealthApiController(HealthRecordRepository healthRecordRepository, UserRepository userRepository) {
        this.healthRecordRepository = healthRecordRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/{elderId}/records")
    public HealthRecord addRecord(
            @PathVariable Long elderId,
            @RequestBody HealthRecordRequest request) {

        User elder = userRepository.findById(elderId)
                .orElseThrow(() -> new NotFoundException("Elder not found"));

        HealthRecord record = new HealthRecord(
                elder.getId(),
                LocalDateTime.now(),
                request.getHeartRate(),
                request.getSystolicPressure(),
                request.getDiastolicPressure(),
                request.getBloodSugar(),
                request.getNotes()
        );

        return healthRecordRepository.save(record);
    }

    @GetMapping("/{elderId}/records")
    public List<HealthRecord> getRecords(@PathVariable Long elderId) {
        User elder = userRepository.findById(elderId)
                .orElseThrow(() -> new NotFoundException("Elder not found"));

        return healthRecordRepository.findByElderIdOrderByRecordTimeDesc(elderId);
    }

    public static class HealthRecordRequest {
        private Integer heartRate;
        private Integer systolicPressure;
        private Integer diastolicPressure;
        private Double bloodSugar;
        private String notes;

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
}
