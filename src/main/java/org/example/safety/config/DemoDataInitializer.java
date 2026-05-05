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
 *   用户名 volunteer1 / 密码 123456  角色 VOLUNTEER（小李）
 */
@Component
public class DemoDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ElderChildLinkRepository elderChildLinkRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final ActivityRepository activityRepository;
    private final VolunteerServiceRepository volunteerServiceRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public DemoDataInitializer(
            UserRepository userRepository,
            ElderChildLinkRepository elderChildLinkRepository,
            EmergencyContactRepository emergencyContactRepository,
            ActivityRepository activityRepository,
            VolunteerServiceRepository volunteerServiceRepository
    ) {
        this.userRepository = userRepository;
        this.elderChildLinkRepository = elderChildLinkRepository;
        this.emergencyContactRepository = emergencyContactRepository;
        this.activityRepository = activityRepository;
        this.volunteerServiceRepository = volunteerServiceRepository;
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
        User volunteer = userRepository.save(
                new User("volunteer1", "小李", encoder.encode("123456"), UserRole.VOLUNTEER));

        elder.setPhone("13800000001");
        child.setPhone("13800000002");
        volunteer.setPhone("13800000003");
        userRepository.save(elder);
        userRepository.save(child);
        userRepository.save(volunteer);

        elderChildLinkRepository.save(new ElderChildLink(elder, child));

        emergencyContactRepository.save(new EmergencyContact(elder, "儿子小王", "13800000000", 1));
        emergencyContactRepository.save(new EmergencyContact(elder, "社区值班", "02112345678", 2));

        activityRepository.save(new Activity("社区健康讲座", "邀请专家讲解老年健康知识", LocalDateTime.now().plusDays(7), "社区活动中心", 50));
        activityRepository.save(new Activity("太极拳晨练", "每周二、四上午太极拳练习", LocalDateTime.now().plusDays(3), "社区花园", 30));
        activityRepository.save(new Activity("手工DIY活动", "制作手工饰品，增进交流", LocalDateTime.now().plusDays(10), "社区活动室", 20));

        volunteerServiceRepository.save(new VolunteerService("陪同买菜", "陪同老人前往社区超市采购生活用品", 10));
        volunteerServiceRepository.save(new VolunteerService("上门测血压", "志愿者上门协助测量血压并记录", 12));
        volunteerServiceRepository.save(new VolunteerService("手机使用指导", "帮助老人学习微信、支付和视频通话", 8));
    }
}
