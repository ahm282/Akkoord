package com.ahm282.Akkoord.controller.auth;

import com.ahm282.Akkoord.dto.request.LoginDTO;
import com.ahm282.Akkoord.model.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private com.ahm282.Akkoord.security.jwt.JwtTokenProvider tokenProvider;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "Password123!";
    private static final String TEST_NAME = "Test User";
    private static final String TEST_TOKEN = "test.jwt.token";

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        UserPrincipal userPrincipal = new UserPrincipal(
                UUID.randomUUID(),
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_NAME,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_VIEWER"))
        );

        authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(any(Authentication.class))).thenReturn(TEST_TOKEN);
    }

    @Test
    @DisplayName("Successful login should return token and user details")
    void loginSuccess() throws Exception {
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(TEST_TOKEN)))
                .andExpect(jsonPath("$.email", is(TEST_EMAIL)))
                .andExpect(jsonPath("$.fullName", is(TEST_NAME)));

        verify(authenticationManager).authenticate(
                argThat(auth ->
                        TEST_EMAIL.equals(auth.getPrincipal()) &&
                                TEST_PASSWORD.equals(auth.getCredentials())
                )
        );
        verify(tokenProvider).generateToken(authentication);
    }

    @Test
    @DisplayName("Login with invalid credentials should return 401")
    void loginInvalidCredentials() throws Exception {
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login with missing email should return 400")
    void loginMissingEmail() throws Exception {
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setPassword(TEST_PASSWORD);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login with missing password should return 400")
    void loginMissingPassword() throws Exception {
        LoginDTO loginRequest = new LoginDTO();
        loginRequest.setEmail(TEST_EMAIL);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
}
