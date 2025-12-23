package com.example.gks.config;

import com.example.gks.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BootstrapConfig {

    @Bean
    CommandLineRunner initUsers(UserService userService) {
        return args -> userService.ensureAdmin();
    }
}
