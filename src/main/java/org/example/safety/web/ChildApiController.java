package org.example.safety.web;

import org.example.safety.domain.Alert;
import org.example.safety.service.SafetyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/children")
public class ChildApiController {
    private final SafetyService safetyService;

    public ChildApiController(SafetyService safetyService) {
        this.safetyService = safetyService;
    }

    @GetMapping("/{childId}/elders/status/today")
    public List<SafetyService.ElderDailyStatus> elderStatusesToday(@PathVariable long childId) {
        return safetyService.listChildElderStatusesToday(childId);
    }

    @GetMapping("/{childId}/alerts/today")
    public List<AlertResponse> alertsToday(@PathVariable long childId) {
        List<Alert> alerts = safetyService.listMissingCheckinAlertsTodayForChild(childId);
        return alerts.stream()
                .map(a -> new AlertResponse(
                        a.getId(),
                        a.getElder().getId(),
                        a.getElder().getName(),
                        a.getAlertDate().toString(),
                        a.getType().name(),
                        a.getCreatedAt().toString()
                ))
                .toList();
    }

    public record AlertResponse(
            long id,
            long elderId,
            String elderName,
            String date,
            String type,
            String createdAt
    ) {
    }
}
