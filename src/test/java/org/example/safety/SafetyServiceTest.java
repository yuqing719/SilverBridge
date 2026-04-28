package org.example.safety;

import org.example.safety.domain.AlertType;
import org.example.safety.repo.AlertRepository;
import org.example.safety.repo.CheckinRepository;
import org.example.safety.repo.UserRepository;
import org.example.safety.service.AlreadyCheckedInException;
import org.example.safety.service.SafetyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class SafetyServiceTest {

    @Autowired
    SafetyService safetyService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CheckinRepository checkinRepository;

    @Autowired
    AlertRepository alertRepository;

    @Test
    void elder_can_checkin_only_once_per_day() {
        long elderId = 1L;

        safetyService.checkinToday(elderId);
        assertThrows(AlreadyCheckedInException.class, () -> safetyService.checkinToday(elderId));

        assertThat(checkinRepository.findAll()).hasSize(1);
    }

    @Test
    void missing_checkin_alert_is_generated_and_resolved_after_checkin() {
        long elderId = 1L;
        LocalDate today = LocalDate.now(java.time.ZoneId.of("Asia/Shanghai"));

        int created = safetyService.generateMissingCheckinAlerts(today);
        assertThat(created).isGreaterThanOrEqualTo(1);

        var alertOpt = alertRepository.findAll().stream()
                .filter(a -> a.getElder().getId() == elderId)
                .filter(a -> a.getAlertDate().equals(today))
                .filter(a -> a.getType() == AlertType.MISSING_CHECKIN)
                .findFirst();

        assertThat(alertOpt).isPresent();
        assertThat(alertOpt.get().isActive()).isTrue();

        safetyService.checkinToday(elderId);

        var refreshed = alertRepository.findById(alertOpt.get().getId()).orElseThrow();
        assertThat(refreshed.isActive()).isFalse();
    }
}
