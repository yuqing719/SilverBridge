package org.example.safety.config;

import org.example.safety.domain.*;
import org.example.safety.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoDataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ElderChildLinkRepository elderChildLinkRepository;
    private final EmergencyContactRepository emergencyContactRepository;

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

        User elder = userRepository.save(new User("王阿姨", UserRole.ELDER));
        User child = userRepository.save(new User("小王", UserRole.CHILD));

        elderChildLinkRepository.save(new ElderChildLink(elder, child));

        emergencyContactRepository.save(new EmergencyContact(elder, "儿子小王", "13800000000", 1));
        emergencyContactRepository.save(new EmergencyContact(elder, "社区值班", "02112345678", 2));
    }
}
