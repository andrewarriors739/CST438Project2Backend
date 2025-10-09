package com.group6.backend;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HealthControllerIT {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void healthEndpointReturnsOkStatus() {
        String url = "http://localhost:" + port + "/api/health";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Assert that status code is 200 OK
        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        // Assert that response body contains expected JSON
        assertThat(response.getBody()).contains("\"status\":\"ok\"");
    }
}
