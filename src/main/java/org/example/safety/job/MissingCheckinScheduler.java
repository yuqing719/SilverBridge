package org.example.safety.job;

import org.example.safety.service.SafetyService;
import org.example.safety.service.TimeProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MissingCheckinScheduler {
    private final SafetyService safetyService;
    private final TimeProvider timeProvider;

    public MissingCheckinScheduler(SafetyService safetyService, TimeProvider timeProvider) {
        this.safetyService = safetyService;
        this.timeProvider = timeProvider;
    }

    // 每天 20:00 自动检测未打卡
    @Scheduled(cron = "0 0 20 * * *", zone = "Asia/Shanghai")
    public void run() {
        LocalDate today = timeProvider.today();
        safetyService.generateMissingCheckinAlerts(today);
    }
}
