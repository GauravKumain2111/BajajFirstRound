package com.bajajhealth.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {
    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    private static final String TOKEN_URL = "https://bfhldevapigw.healthrx.co.in/hiring/oauth/token";
    private static final String WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
    // Updated SQL as per SQL Question 1 JAVA
    private static final String FINAL_QUERY = "SELECT d.department_name AS department, COUNT(e.employee_id) AS num_employees, AVG(e.salary) AS avg_salary FROM department d JOIN employee e ON d.department_id = e.department_id GROUP BY d.department_name HAVING COUNT(e.employee_id) > 5 ORDER BY avg_salary DESC;";

    private final RestTemplate restTemplate;

    public WebhookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String startProcess() {
        try {
            // Step 1: Token request
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", "John Doe");
            requestBody.put("email", "john@example.com");
            requestBody.put("regNo", "REG12347");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(TOKEN_URL, request, (Class<Map<String, Object>>)(Class<?>)Map.class);
            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !responseBody.containsKey("accessToken")) {
                logger.error("Token response missing accessToken: {}", responseBody);
                return "Token response missing accessToken.";
            }
            String accessToken = Objects.toString(responseBody.get("accessToken"), null);
            logger.info("Webhook: {}", WEBHOOK_URL);
            logger.info("Access Token: {}", accessToken);

            // Step 2: SQL query
            Map<String, String> body = new HashMap<>();
            body.put("query", FINAL_QUERY);

            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.setContentType(MediaType.APPLICATION_JSON);
            authHeaders.setBearerAuth(accessToken);

            HttpEntity<Map<String, String>> finalRequest = new HttpEntity<>(body, authHeaders);

            // Step 3: Submit SQL query to webhook
            ResponseEntity<String> submitResponse = restTemplate.postForEntity(
                WEBHOOK_URL,
                finalRequest,
                String.class
            );

            logger.info("Response from webhook: {}", submitResponse.getBody());
            return submitResponse.getBody();

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                logger.error("Resource not found (404) at endpoint: {}. Response body: {}", TOKEN_URL, e.getResponseBodyAsString());
                return "Error: Resource not found (404) at endpoint. Please check the URL or instructions.";
            } else {
                logger.error("Client error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
                return "Client error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
            }
        } catch (HttpServerErrorException e) {
            logger.error("Server error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return "Server error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        } catch (ResourceAccessException e) {
            logger.error("Resource access error: {}", e.getMessage());
            return "Resource access error: " + e.getMessage();
        } catch (Exception e) {
            logger.error("Exception occurred while processing webhook:", e);
            return "Exception occurred: " + e.getMessage();
        }
    }
}
