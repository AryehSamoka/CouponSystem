package com.aryeh.CouponSystem.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RestConfiguration {

    @Bean(name = "tokens")
    public Map<String, ClientSession> tokensMap() {
        return new ConcurrentHashMap<>();
    }
}
