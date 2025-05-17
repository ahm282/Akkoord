package com.ahm282.Akkoord.controller.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LogoutTests {

    @Autowired
    private MockMvc mockMvc;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        String mockEmail = "ahmed@example.com";
        String mockPassword = "password123";

        String loginResponse = mockMvc.perform(
                        post("/auth/login")
                                .contentType("application/json")
                                .content("{\"email\":\"" + mockEmail + "\", \"password\":\"" + mockPassword + "\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Parse token from response JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(loginResponse);
        authToken = node.get("token").asText();
    }

    @Test
    @DisplayName("Successful logout should return 200 OK")
    void logout() throws Exception {
        mockMvc.perform(get("/auth/logout")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }
}
