package org.example.safety.config;

import org.example.safety.domain.*;
import org.example.safety.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 启动时写入演示数据（仅在数据库为空时执行）。
 * 演示账号：
 *   用户名 elder1 / 密码 123456  角色 ELDER（王阿姨）
 *   用户名 child1 / 密码 123456  角色 CHILD（小王）
 */
@Component
public class DemoDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ElderChildLinkRepository elderChildLinkRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final ActivityRepository activityRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public DemoDataInitializer(
            UserRepository userRepository,
            ElderChildLinkRepository elderChildLinkRepository,
            EmergencyContactRepository emergencyContactRepository,
            ActivityRepository activityRepository
    ) {
        this.userRepository = userRepository;
        this.elderChildLinkRepository = elderChildLinkRepository;
        this.emergencyContactRepository = emergencyContactRepository;
        this.activityRepository = activityRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User elder = userRepository.save(
                new User("elder1", "王阿姨", encoder.encode("123456"), UserRole.ELDER));
        User child = userRepository.save(
                new User("child1", "小王", encoder.encode("123456"), UserRole.CHILD));

        elder.setPhone("13800000001");
        child.setPhone("13800000002");
        userRepository.save(elder);
        userRepository.save(child);

        elderChildLinkRepository.save(new ElderChildLink(elder, child));

        emergencyContactRepository.save(new EmergencyContact(elder, "儿子小王", "13800000000", 1));
        emergencyContactRepository.save(new EmergencyContact(elder, "社区值班", "02112345678", 2));

        // Add demo activities
        activityRepository.save(new Activity("社区健康讲座", "邀请专家讲解老年健康知识", LocalDateTime.now().plusDays(7), "社区活动中心", 50));
        activityRepository.save(new Activity("太极拳晨练", "每周二、四上午太极拳练习", LocalDateTime.now().plusDays(3), "社区花园", 30));
        activityRepository.save(new Activity("手工DIY活动", "制作手工饰品，增进交流", LocalDateTime.now().plusDays(10), "社区活动室", 20));
    }
}
