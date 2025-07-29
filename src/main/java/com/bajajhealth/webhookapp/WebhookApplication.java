package com.bajajhealth.webhookapp;

import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class WebhookApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WebhookApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
            Map<String, String> requestBody = Map.of(
                "name", "Gaurav Luthra",
                "regNo", "2210990317",
                "email", "gaurav317.be22@chitkara.edu.in"
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(generateUrl, requestBody, Map.class);
            String token = (String) response.getBody().get("accessToken");
            System.out.println("Access Token: " + token);

            if (token == null) {
                System.err.println("Failed to retrieve access token.");
                return;
            }

            String finalQuery = "SELECT p.AMOUNT AS SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
                                "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, d.DEPARTMENT_NAME " +
                                "FROM PAYMENTS p " +
                                "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                                "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                                "WHERE DAY(p.PAYMENT_TIME) != 1 " +
                                "ORDER BY p.AMOUNT DESC LIMIT 1;";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", token);

            Map<String, String> sqlPayload = Map.of("finalQuery", finalQuery);
            HttpEntity<Map<String, String>> submissionRequest = new HttpEntity<>(sqlPayload, headers);

            String submissionUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
            ResponseEntity<String> result = restTemplate.postForEntity(submissionUrl, submissionRequest, String.class);

            System.out.println("Submit Response: " + result.getStatusCode());
            System.out.println("Submit Response Body: " + result.getBody());
            System.out.println("✅ SQL query submitted successfully.");
        } catch (Exception e) {
            System.err.println("❌ Submission failed: " + e.getMessage());
        }
    }
}
