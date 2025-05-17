package com.ahm282.Akkoord.controller.auth;

import com.ahm282.Akkoord.dto.request.RegisterDTO;
import com.ahm282.Akkoord.model.entity.AppUser;
import com.ahm282.Akkoord.model.entity.Department;
import com.ahm282.Akkoord.model.enums.UserAccessLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RegisterTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private com.ahm282.Akkoord.repository.AppUserRepository userRepository;

    @MockitoBean
    private com.ahm282.Akkoord.repository.DepartmentRepository departmentRepository;

    @MockitoBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "Password123!";
    private static final String TEST_NAME = "Test User";

    private RegisterDTO registerRequest;
    private AppUser mockUser;
    private Department mockDepartment;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterDTO();
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASSWORD);
        registerRequest.setFullName(TEST_NAME);
        registerRequest.setRole("VIEWER");

        mockUser = new AppUser();
        mockUser.setId(UUID.randomUUID());
        mockUser.setEmail(TEST_EMAIL);
        mockUser.setFullName(TEST_NAME);
        mockUser.setRole(UserAccessLevel.VIEWER);

        mockDepartment = new Department();
        mockDepartment.setId(UUID.randomUUID());
        mockDepartment.setName("Test Department");

        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(AppUser.class))).thenReturn(mockUser);
    }

    @Test
    @DisplayName("Successful registration should return user details")
    void registerSuccess() throws Exception {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(mockUser.getId().toString())))
                .andExpect(jsonPath("$.fullName", is(TEST_NAME)))
                .andExpect(jsonPath("$.email", is(TEST_EMAIL)))
                .andExpect(jsonPath("$.accessLevel", is("VIEWER")));

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(userCaptor.capture());

        AppUser savedUser = userCaptor.getValue();
        assertEquals(TEST_EMAIL, savedUser.getEmail());
        assertEquals(TEST_NAME, savedUser.getFullName());
        assertEquals(UserAccessLevel.VIEWER, savedUser.getRole());
        assertEquals("encoded_password", savedUser.getPasswordHash());
    }

    @Test
    @DisplayName("Registration with existing email should return 400")
    void registerExistingEmail() throws Exception {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Email is already taken!")));

        verify(userRepository, never()).save(any(AppUser.class));
    }

    @Test
    @DisplayName("Registration with invalid department ID should return 404")
    void registerInvalidDepartment() throws Exception {
        UUID departmentId = UUID.randomUUID();
        registerRequest.setDepartmentId(departmentId);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(registerRequest)))
                .andExpect(status().isNotFound());

        verify(userRepository, never()).save(any(AppUser.class));
    }

    @Test
    @DisplayName("Registration with valid department ID should include department")
    void registerWithDepartment() throws Exception {
        UUID departmentId = UUID.randomUUID();
        registerRequest.setDepartmentId(departmentId);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(mockDepartment));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(userCaptor.capture());

        AppUser savedUser = userCaptor.getValue();
        assertEquals(mockDepartment, savedUser.getDepartment());
    }

    @Test
    @DisplayName("Registration with missing email should return 400")
    void registerMissingEmail() throws Exception {
        RegisterDTO incompleteRequest = new RegisterDTO();
        incompleteRequest.setPassword(TEST_PASSWORD);
        incompleteRequest.setFullName(TEST_NAME);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(incompleteRequest)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(AppUser.class));
    }

    @Test
    @DisplayName("Registration with missing password should return 400")
    void registerMissingPassword() throws Exception {
        RegisterDTO incompleteRequest = new RegisterDTO();
        incompleteRequest.setEmail(TEST_EMAIL);
        incompleteRequest.setFullName(TEST_NAME);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(incompleteRequest)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(AppUser.class));
    }

    @Test
    @DisplayName("Registration with missing name should return 400")
    void registerMissingName() throws Exception {
        RegisterDTO incompleteRequest = new RegisterDTO();
        incompleteRequest.setEmail(TEST_EMAIL);
        incompleteRequest.setPassword(TEST_PASSWORD);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(incompleteRequest)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(AppUser.class));
    }

    @Test
    @DisplayName("Registration with custom role should set that role")
    void registerWithCustomRole() throws Exception {
        registerRequest.setRole("ADMIN");

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(userCaptor.capture());

        AppUser savedUser = userCaptor.getValue();
        assertEquals(UserAccessLevel.ADMIN, savedUser.getRole());
    }

    @Test
    @DisplayName("Registration without role should default to VIEWER")
    void registerDefaultRole() throws Exception {
        registerRequest.setRole(null);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(userCaptor.capture());

        AppUser savedUser = userCaptor.getValue();
        assertEquals(UserAccessLevel.VIEWER, savedUser.getRole());
    }
}
