package com.platform.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.auth.domain.enums.RoleType;
import com.platform.auth.dto.request.ChangeRoleRequest;
import com.platform.auth.dto.request.LoginRequest;
import com.platform.auth.dto.request.RegisterRequest;
import com.platform.auth.dto.request.UpdateProfileRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String obtainToken(String email, String password) throws Exception {
        LoginRequest loginReq = new LoginRequest(email, password);
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();
    }

    @Test
    void shouldEnforceRbacAndAllowProfileUpdates() throws Exception {
        // 1. Register a standard user
        RegisterRequest registerReq = new RegisterRequest();
        registerReq.setEmail("bob.user@example.co.uk");
        registerReq.setPassword("Password@123");
        registerReq.setFirstName("Bob");
        registerReq.setLastName("Jones");
        registerReq.setPhoneNumber("+447700900088");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        String userToken = obtainToken("bob.user@example.co.uk", "Password@123");
        String adminToken = obtainToken("testadmin@platform.com", "Admin@123456");

        // 2. Fetch current user profile (/me)
        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("bob.user@example.co.uk"))
                .andExpect(jsonPath("$.data.firstName").value("Bob"));

        // 3. Update user profile
        UpdateProfileRequest updateReq = new UpdateProfileRequest();
        updateReq.setFirstName("Robert");
        updateReq.setLastName("Jones");
        updateReq.setPhoneNumber("+447700900999");

        mockMvc.perform(put("/api/v1/users/me")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Robert"));

        // 4. Standard user accessing Admin endpoint -> 403 Forbidden
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        // 5. Admin user accessing Admin endpoint -> 200 OK
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()", greaterThanOrEqualTo(2)));
    }
}
