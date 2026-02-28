package com.ethan.byztine.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public String ping() {
        return "Imperium Byzantinum online";
    }
}