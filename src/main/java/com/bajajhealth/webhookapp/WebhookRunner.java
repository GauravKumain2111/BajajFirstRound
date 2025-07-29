package com.bajajhealth.webhookapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bajajhealth.model.WebhookResponse;

@Component
public class WebhookRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

     
        String genUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        String requestJson = """
            {
              "name": "Gaurav Kumain",
              "regNo": "REG12347",
              "email": "your-email@example.com"
            }
        """;

        HttpHeaders genHeaders = new HttpHeaders();
        genHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> genEntity = new HttpEntity<>(requestJson, genHeaders);

        ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(genUrl, genEntity, WebhookResponse.class);
        String webhookUrl = response.getBody().getWebhook();
        String accessToken = response.getBody().getAccessToken();

        System.out.println("Webhook URL: " + webhookUrl);
        System.out.println("Access Token: " + accessToken);

     
        String finalQuery = "SELECT p.AMOUNT AS SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, d.DEPARTMENT_NAME FROM PAYMENTS p JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID WHERE DAY(p.PAYMENT_TIME) != 1 ORDER BY p.AMOUNT DESC LIMIT 1";


     
        String escapedQuery = finalQuery.replace("\"", "\\\"").trim();
        System.out.println("Escaped Final Query: " + escapedQuery);
        String body = String.format("{\"finalQuery\": \"%s\"}", escapedQuery);
        System.out.println("Request Body: " + body);

        HttpHeaders submitHeaders = new HttpHeaders();
        submitHeaders.setContentType(MediaType.APPLICATION_JSON);
        submitHeaders.setBearerAuth(accessToken);

        HttpEntity<String> submitEntity = new HttpEntity<>(body, submitHeaders);

        String submitUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
        ResponseEntity<String> submitResponse = restTemplate.postForEntity(submitUrl, submitEntity, String.class);

        System.out.println("✅ Submission Response: " + submitResponse.getBody());
    }
}
