package org.example.safety.service;

import org.springframework.stereotype.Component;

import java.time.*;

@Component
public class TimeProvider {
    private final ZoneId zoneId = ZoneId.of("Asia/Shanghai");

    public ZoneId zoneId() {
        return zoneId;
    }

    public LocalDate today() {
        return LocalDate.now(zoneId);
    }

    public LocalDateTime now() {
        return LocalDateTime.now(zoneId);
    }
}
