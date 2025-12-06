package org.example.expert.domain.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.example.expert.common.utils.JwtUtil;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.fixtures.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserAdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private User user;
    private String token;


    @BeforeEach
    void setUp() {
        user = UserFixture.createTestUserUserRole();
        userRepository.save(user);

        token = jwtUtil.createToken(2L, "admin@test.com", UserRole.ADMIN);
    }

    @Test
    @Order(1)
    @DisplayName("PATCH /admin/users/{userId} 통합 테스트 - 유저 권한 변경 요청 성공")
    void changeUserRole_success() throws Exception {

        // Given
        String requestBody =
            """
            {
                "role" : "ADMIN"
            }
            """;

        // When & Then
        mockMvc.perform(patch(String.format("/admin/users/%d", user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(requestBody))
            .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    @DisplayName("PATCH /admin/users/{userId} 통합 테스트 - 실패: Validation 작동 확인")
    void changeUserRole_failure_NullRole() throws Exception {

        // Given
        String requestBody =
            """
            {
                "role" : ""
            }
            """;

        // When & Then
        mockMvc.perform(patch(String.format("/admin/users/%d", user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }
}