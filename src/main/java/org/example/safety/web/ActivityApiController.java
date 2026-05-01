package org.example.safety.web;

import jakarta.servlet.http.HttpSession;
import org.example.safety.service.ActivityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 活动相关 API
 */
@RestController
@RequestMapping("/api/activities")
public class ActivityApiController {

    private final ActivityService activityService;

    public ActivityApiController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public List<Map<String, Object>> getActivities(HttpSession session) {
        long userId = SessionUtil.getRequiredUserId(session);
        return activityService.getActivitiesWithRegistrationStatus(userId);
    }

    @PostMapping("/{activityId}/register")
    public void registerForActivity(@PathVariable long activityId, HttpSession session) {
        long userId = SessionUtil.getRequiredUserId(session);
        activityService.registerForActivity(userId, activityId);
    }

    @PostMapping("/{activityId}/checkin")
    public void checkin(@PathVariable long activityId, HttpSession session) {
        long userId = SessionUtil.getRequiredUserId(session);
        activityService.checkinForActivity(userId, activityId);
    }
}
