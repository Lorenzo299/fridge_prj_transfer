package com.gdserv.fridge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gdserv.fridge.notifications.ProductNotifier;

@Configuration
public class AppConfig {

    // BEAN PER IL NOTIFIER
    @Bean
    public ProductNotifier productNotifier() {
        
        return new ProductNotifier();
    }

}
