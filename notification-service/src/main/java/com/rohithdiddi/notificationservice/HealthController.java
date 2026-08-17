package com.rohithdiddi.notificationservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/notifications/health")
    public String health() {
        return "notification-service is up";
    }
}
