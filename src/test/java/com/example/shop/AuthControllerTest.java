package com.example.shop;

import com.example.shop.dto.AuthResponse;
import com.example.shop.dto.LoginRequest;
import com.example.shop.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

    @LocalServerPort
    private int port;

    private WebTestClient client() {
        return WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    public void testRegisterAndLogin() {
        RegisterRequest register = new RegisterRequest();
        register.setUsername("testuser_junit");
        register.setPassword("password123");
        register.setEmail("test_junit@example.com");
        register.setFullName("Test User");

        AuthResponse registerResponse = client().post()
                .uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(register)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(registerResponse);
        assertNotNull(registerResponse.getToken());

        LoginRequest login = new LoginRequest();
        login.setUsername("testuser_junit");
        login.setPassword("password123");

        AuthResponse loginResponse = client().post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(login)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getToken());
    }

    @Test
    public void testLoginWithWrongPassword() {
        LoginRequest login = new LoginRequest();
        login.setUsername("testuser_junit");
        login.setPassword("wrong_password");

        client().post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(login)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}