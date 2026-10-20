package com.lapalma.management.shared.api;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;


@RestController 
public class SystemController {

    @GetMapping("/api/v1/system")
    public Map<String, String> getSystemInfo() {
        return Map.of(
            "application", "La palma management",
            "status", "running",
            "apiVersion", "v1"
        );
    }
    
    
}
