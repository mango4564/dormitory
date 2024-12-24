package com.kaiyu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestModeConfig {
    
    @Value("${test.mode:false}")
    private boolean testMode;
    
    public boolean isTestMode() {
        return testMode;
    }
} 