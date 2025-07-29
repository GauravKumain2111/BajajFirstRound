package com.bajajhealth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bajajhealth.service.WebhookService;

@SpringBootApplication
public class WebhookappApplication implements CommandLineRunner {
    private final WebhookService webhookService;

    public WebhookappApplication(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    public static void main(String[] args) {
        SpringApplication.run(WebhookappApplication.class, args);
    }

    @Override
    public void run(String... args) {
        webhookService.startProcess();
    }
}
