package org.example.expert.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.example.expert.common.exception.InvalidRequestException;
import org.example.expert.common.utils.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.fixtures.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    User user;

    @BeforeEach
    void setUp() {
        user = UserFixture.createTestUserUserRole();
        userRepository.save(user);
    }

    @Test
    @DisplayName("유저 정보 조회 통합 테스트")
    void getUser_success() {

        // Given
        long userId = 1L;

        // When
        UserResponse response = userService.getUser(userId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("유저 정보 조회 통합 테스트 - 존재하지 않는 유저ID 조회")
    void getUser_failure() {

        // Given
        long userId = 2L;

        // When & Then
        assertThatThrownBy(() -> userService.getUser(userId))
            .isInstanceOf(InvalidRequestException.class)
            .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("비밀번호 변경 통합 테스트")
    void changePassword_success() {

        // Given
        long userId = 1L;

        String oldPassword = UserFixture.DEFAULT_PASSWORD;
        String newPassword = "QWER1234";

        UserChangePasswordRequest request = new UserChangePasswordRequest(oldPassword, newPassword);

        // When
        userService.changePassword(userId, request);

        // Then
        assertThat(passwordEncoder.matches(newPassword, user.getPassword())).isTrue();
    }

    @Test
    @DisplayName("비밀번호 변경 통합 테스트 - 존재하지 않는 유저ID")
    void changePassword_failure_NotFoundUserId() {

        // Given
        long userId = 2L;

        String oldPassword = UserFixture.DEFAULT_PASSWORD;
        String newPassword = "QWER1234";

        UserChangePasswordRequest request = new UserChangePasswordRequest(oldPassword, newPassword);

        // When & Then
        assertThatThrownBy(() -> userService.changePassword(userId, request))
            .isInstanceOf(InvalidRequestException.class)
            .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("비밀번호 변경 통합 테스트 - 현재 비밀번호와 새 비밀번호 일치")
    void changePassword_failure_SamePassword() {

        // Given
        long userId = 1L;

        String oldPassword = UserFixture.DEFAULT_PASSWORD;
        String newPassword = UserFixture.DEFAULT_PASSWORD;

        UserChangePasswordRequest request = new UserChangePasswordRequest(oldPassword, newPassword);

        // When & Then
        assertThatThrownBy(() -> userService.changePassword(userId, request))
            .isInstanceOf(InvalidRequestException.class)
            .hasMessageContaining("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
    }

    @Test
    @DisplayName("비밀번호 변경 통합 테스트 - 현재 비밀번호와 새 비밀번호 일치")
    void changePassword_failure_WrongPassword() {

        // Given
        long userId = 1L;

        String oldPassword = "12345678";
        String newPassword = "QWER1234";

        UserChangePasswordRequest request = new UserChangePasswordRequest(oldPassword, newPassword);

        // When & Then
        assertThatThrownBy(() -> userService.changePassword(userId, request))
            .isInstanceOf(InvalidRequestException.class)
            .hasMessageContaining("잘못된 비밀번호입니다.");
    }
}