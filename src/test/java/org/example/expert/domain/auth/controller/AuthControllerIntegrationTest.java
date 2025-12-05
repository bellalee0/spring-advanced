package org.example.expert.domain.auth.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.example.expert.common.utils.JwtUtil;
import org.example.expert.common.utils.PasswordEncoder;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.fixtures.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("POST /auth/signup 통합 테스트 - 회원가입 요청 성공")
    void signup() throws Exception {

        // Given
        String requestBody =
            """
            {
                "email" : "test@test.com",
                "password" : "1234",
                "userRole" : "USER"
            }
            """;

        // When & Then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.email").value(UserFixture.DEFAULT_EMAIL))
            .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("POST /auth/signin 통합 테스트 - 로그인 요청 성공")
    void signin() throws Exception {

        // Given
        User user = UserFixture.createTestUserUserRole();
        userRepository.save(user);

        String requestBody =
            """
            {
                "email" : "test@test.com",
                "password" : "1234"
            }
            """;

        // When & Then
        mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bearerToken").isNotEmpty());
    }
}