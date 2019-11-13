package com.aryeh.CouponSystem.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RestConfiguration {

    @Bean(name = "tokens")
    public Map<String, ClientSession> tokensMap() {
        return new HashMap<>();
    }
}
