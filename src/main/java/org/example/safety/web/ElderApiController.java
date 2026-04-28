package org.example.safety.web;

import org.example.safety.domain.Checkin;
import org.example.safety.domain.EmergencyContact;
import org.example.safety.service.SafetyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/elders")
public class ElderApiController {
    private final SafetyService safetyService;

    public ElderApiController(SafetyService safetyService) {
        this.safetyService = safetyService;
    }

    @PostMapping("/{elderId}/checkins/today")
    public CheckinResponse checkinToday(@PathVariable long elderId) {
        Checkin checkin = safetyService.checkinToday(elderId);
        return new CheckinResponse(
                checkin.getElder().getId(),
                checkin.getCheckinDate().toString(),
                checkin.getCheckinTime().toString()
        );
    }

    @GetMapping("/{elderId}/status/today")
    public SafetyService.ElderDailyStatus statusToday(@PathVariable long elderId) {
        return safetyService.getElderTodayStatus(elderId);
    }

    @GetMapping("/{elderId}/contacts")
    public List<ContactResponse> contacts(@PathVariable long elderId) {
        List<EmergencyContact> contacts = safetyService.listContacts(elderId);
        return contacts.stream()
                .map(c -> new ContactResponse(c.getId(), c.getName(), c.getPhone(), c.getSortOrder()))
                .toList();
    }

    public record CheckinResponse(long elderId, String date, String checkinTime) {
    }

    public record ContactResponse(long id, String name, String phone, int sortOrder) {
    }
}
