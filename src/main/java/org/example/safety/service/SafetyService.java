package org.example.safety.service;

import org.example.safety.domain.*;
import org.example.safety.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SafetyService {
    private final UserRepository userRepository;
    private final CheckinRepository checkinRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final ElderChildLinkRepository elderChildLinkRepository;
    private final AlertRepository alertRepository;
    private final TimeProvider timeProvider;

    public SafetyService(
            UserRepository userRepository,
            CheckinRepository checkinRepository,
            EmergencyContactRepository emergencyContactRepository,
            ElderChildLinkRepository elderChildLinkRepository,
            AlertRepository alertRepository,
            TimeProvider timeProvider
    ) {
        this.userRepository = userRepository;
        this.checkinRepository = checkinRepository;
        this.emergencyContactRepository = emergencyContactRepository;
        this.elderChildLinkRepository = elderChildLinkRepository;
        this.alertRepository = alertRepository;
        this.timeProvider = timeProvider;
    }

    @Transactional
    public Checkin checkinToday(long elderId) {
        User elder = userRepository.findById(elderId)
                .orElseThrow(() -> new NotFoundException("elder not found: " + elderId));
        if (elder.getRole() != UserRole.ELDER) {
            throw new IllegalArgumentException("user is not elder: " + elderId);
        }

        LocalDate today = timeProvider.today();
        if (checkinRepository.existsByElderAndCheckinDate(elder, today)) {
            throw new AlreadyCheckedInException("already checked in today");
        }

        LocalDateTime now = timeProvider.now();
        Checkin checkin = new Checkin(elder, today, now);
        Checkin saved = checkinRepository.save(checkin);

        // 如果当天已经生成“未打卡预警”，打卡后自动解除
        List<Alert> active = alertRepository.findByElderAndAlertDateAndResolvedAtIsNull(elder, today);
        for (Alert alert : active) {
            if (alert.getType() == AlertType.MISSING_CHECKIN) {
                alert.resolve(now);
            }
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public ElderDailyStatus getElderTodayStatus(long elderId) {
        User elder = userRepository.findById(elderId)
                .orElseThrow(() -> new NotFoundException("elder not found: " + elderId));
        if (elder.getRole() != UserRole.ELDER) {
            throw new IllegalArgumentException("user is not elder: " + elderId);
        }

        LocalDate today = timeProvider.today();
        return buildElderStatus(elder, today);
    }

    @Transactional(readOnly = true)
    public List<EmergencyContact> listContacts(long elderId) {
        User elder = userRepository.findById(elderId)
                .orElseThrow(() -> new NotFoundException("elder not found: " + elderId));
        return emergencyContactRepository.findByElderOrderBySortOrderAsc(elder);
    }

    @Transactional(readOnly = true)
    public List<ElderDailyStatus> listChildElderStatusesToday(long childId) {
        User child = userRepository.findById(childId)
                .orElseThrow(() -> new NotFoundException("child not found: " + childId));
        if (child.getRole() != UserRole.CHILD) {
            throw new IllegalArgumentException("user is not child: " + childId);
        }

        LocalDate today = timeProvider.today();
        List<ElderChildLink> links = elderChildLinkRepository.findByChild(child);
        List<ElderDailyStatus> result = new ArrayList<>();
        for (ElderChildLink link : links) {
            result.add(buildElderStatus(link.getElder(), today));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<Alert> listMissingCheckinAlertsTodayForChild(long childId) {
        // 当前最小实现：子女只看自己绑定的老人里，哪些老人今天存在 active 的 MISSING_CHECKIN
        User child = userRepository.findById(childId)
                .orElseThrow(() -> new NotFoundException("child not found: " + childId));
        if (child.getRole() != UserRole.CHILD) {
            throw new IllegalArgumentException("user is not child: " + childId);
        }

        LocalDate today = timeProvider.today();
        List<ElderChildLink> links = elderChildLinkRepository.findByChild(child);
        List<Alert> alerts = new ArrayList<>();
        for (ElderChildLink link : links) {
            alerts.addAll(alertRepository.findByElderAndAlertDateAndResolvedAtIsNull(link.getElder(), today));
        }
        alerts.removeIf(a -> a.getType() != AlertType.MISSING_CHECKIN);
        return alerts;
    }

    @Transactional
    public int generateMissingCheckinAlerts(LocalDate date) {
        List<User> elders = userRepository.findByRole(UserRole.ELDER);
        int created = 0;
        LocalDateTime now = timeProvider.now();

        for (User elder : elders) {
            boolean checkedIn = checkinRepository.existsByElderAndCheckinDate(elder, date);
            if (checkedIn) {
                continue;
            }

            Alert alert = alertRepository.findByElderAndAlertDateAndType(elder, date, AlertType.MISSING_CHECKIN)
                    .orElse(null);
            if (alert == null) {
                alertRepository.save(new Alert(elder, date, AlertType.MISSING_CHECKIN, now));
                created++;
            } else if (!alert.isActive()) {
                // 历史上创建过但已解除：同一天不重复激活（保持简单）
            }
        }

        return created;
    }

    private ElderDailyStatus buildElderStatus(User elder, LocalDate date) {
        Checkin checkin = checkinRepository.findByElderAndCheckinDate(elder, date).orElse(null);
        boolean checkedIn = checkin != null;
        LocalDateTime checkinTime = checkedIn ? checkin.getCheckinTime() : null;

        Alert missingAlert = alertRepository.findByElderAndAlertDateAndType(elder, date, AlertType.MISSING_CHECKIN)
                .orElse(null);
        boolean missingCheckinAlertActive = missingAlert != null && missingAlert.isActive();

        return new ElderDailyStatus(
                elder.getId(),
                elder.getName(),
                date,
                checkedIn,
                checkinTime,
                missingCheckinAlertActive
        );
    }

    public record ElderDailyStatus(
            long elderId,
            String elderName,
            LocalDate date,
            boolean checkedIn,
            LocalDateTime checkinTime,
            boolean missingCheckinAlert
    ) {
    }
}
