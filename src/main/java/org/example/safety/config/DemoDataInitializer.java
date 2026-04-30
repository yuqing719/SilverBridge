package org.example.safety.config;

import org.example.safety.domain.*;
import org.example.safety.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

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
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public DemoDataInitializer(
            UserRepository userRepository,
            ElderChildLinkRepository elderChildLinkRepository,
            EmergencyContactRepository emergencyContactRepository
    ) {
        this.userRepository = userRepository;
        this.elderChildLinkRepository = elderChildLinkRepository;
        this.emergencyContactRepository = emergencyContactRepository;
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
    }
}
