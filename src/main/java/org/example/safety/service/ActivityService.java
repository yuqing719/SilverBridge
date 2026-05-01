package org.example.safety.service;

import org.example.safety.domain.*;
import org.example.safety.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityRegistrationRepository registrationRepository;
    private final ActivityCheckinRepository checkinRepository;
    private final UserRepository userRepository;

    public ActivityService(ActivityRepository activityRepository,
                          ActivityRegistrationRepository registrationRepository,
                          ActivityCheckinRepository checkinRepository,
                          UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.registrationRepository = registrationRepository;
        this.checkinRepository = checkinRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getActivitiesWithRegistrationStatus(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("用户不存在: " + userId));

        List<Activity> activities = activityRepository.findAllByOrderByDateAsc();
        List<ActivityRegistration> registrations = registrationRepository.findByUser(user);

        Map<Long, ActivityRegistration> registrationMap = registrations.stream()
                .collect(Collectors.toMap(reg -> reg.getActivity().getId(), reg -> reg));

        return activities.stream().map(activity -> {
            boolean registered = registrationMap.containsKey(activity.getId());
            return Map.<String, Object>of(
                    "id", activity.getId(),
                    "title", activity.getTitle(),
                    "description", activity.getDescription(),
                    "date", activity.getDate(),
                    "location", activity.getLocation(),
                    "maxParticipants", activity.getMaxParticipants(),
                    "currentParticipants", activity.getCurrentParticipants(),
                    "registered", registered
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public void registerForActivity(long userId, long activityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("用户不存在: " + userId));
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("活动不存在: " + activityId));

        if (registrationRepository.existsByUserAndActivity(user, activity)) {
            throw new IllegalArgumentException("已经报名此活动");
        }

        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new IllegalArgumentException("活动名额已满");
        }

        ActivityRegistration registration = new ActivityRegistration(user, activity);
        registrationRepository.save(registration);

        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        activityRepository.save(activity);
    }

    @Transactional
    public void checkinForActivity(long userId, long activityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("用户不存在: " + userId));
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new NotFoundException("活动不存在: " + activityId));

        if (!registrationRepository.existsByUserAndActivity(user, activity)) {
            throw new IllegalArgumentException("未报名此活动，无法签到");
        }

        if (checkinRepository.existsByUserAndActivity(user, activity)) {
            throw new IllegalArgumentException("已经签到此活动");
        }

        ActivityCheckin checkin = new ActivityCheckin(user, activity);
        checkinRepository.save(checkin);
    }
}
