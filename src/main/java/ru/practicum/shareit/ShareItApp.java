package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "ru.practicum.shareit.booking.repository")
public class ShareItApp {
    public static void main(String[] args) {
        SpringApplication.run(ShareItApp.class, args);
    }
}