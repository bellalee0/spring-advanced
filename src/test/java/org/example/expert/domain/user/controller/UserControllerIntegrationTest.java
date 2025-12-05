package org.example.expert.domain.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.example.expert.common.utils.JwtUtil;
import org.example.expert.common.utils.PasswordEncoder;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.fixtures.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private String token;


    @BeforeEach
    void setUp() {
        user = UserFixture.createTestUserUserRole();
        userRepository.save(user);

        token = jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole());
    }

    @Test
    @DisplayName("GET /users/{userId} 통합 테스트 - 유저 조회 성공")
    void getUser_success() throws Exception {

        // When & Then
        mockMvc.perform(get(String.format("/users/%d", user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(user.getId()))
            .andExpect(jsonPath("$.email").value(UserFixture.DEFAULT_EMAIL));
    }

    @Test
    @DisplayName("PUT /users 통합 테스트 - 유저 비밀번호 변경 성공")
    void changePassword_success() throws Exception {

        // Given
        String requestBody =
            """
            {
                "oldPassword" : "1234",
                "newPassword" : "QWER1234"
            }
            """;

        // When & Then
        mockMvc.perform(put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(requestBody))
            .andExpect(status().isOk());
    }
}