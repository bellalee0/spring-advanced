package org.example.expert.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.example.expert.common.exception.InvalidRequestException;
import org.example.expert.common.utils.PasswordEncoder;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.fixtures.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    @Test
    @DisplayName("유저 조회 - 성공: 반환 내용 확인")
    void getUser_success() {

        // Given
        User user = UserFixture.createTestUserUserRole();
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        UserResponse response = userService.getUser(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo(UserFixture.DEFAULT_EMAIL);
    }

    @Test
    @DisplayName("유저 조회 - 실패: 존재하지 않는 유저ID")
    void getUser_failure_NotFoundUserId() {

        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> userService.getUser(1L));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공: 예외 발생하지 않음")
    void changePassword_success() {

        // Given
        User user = UserFixture.createTestUserUserRole();
        ReflectionTestUtils.setField(user, "id", 1L);

        UserChangePasswordRequest request = new UserChangePasswordRequest("12345678", "QWER1234");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).thenReturn(false);
        when(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).thenReturn(true);

        // When & Then
        assertDoesNotThrow(() -> userService.changePassword(1L, request));
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패: 존재하지 않는 유저ID")
    void changePassword_failure_NotFoundUserId() {

        // Given
        UserChangePasswordRequest request = new UserChangePasswordRequest(UserFixture.DEFAULT_PASSWORD, "QWER1234");

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> userService.changePassword(1L, request));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패: 현재 비밀번호와 새 비밀번호 일치")
    void changePassword_failure_SamePassword() {

        // Given
        User user = UserFixture.createTestUserUserRole();
        ReflectionTestUtils.setField(user, "id", 1L);

        UserChangePasswordRequest request = new UserChangePasswordRequest(UserFixture.DEFAULT_PASSWORD, UserFixture.DEFAULT_PASSWORD);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).thenReturn(true);

        // When & Then
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> userService.changePassword(1L, request));
        assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("비밀번호 변경 - 실패: 현재 비밀번호 불일치")
    void changePassword_failure_WrongPassword() {

        // Given
        User user = UserFixture.createTestUserUserRole();
        ReflectionTestUtils.setField(user, "id", 1L);

        UserChangePasswordRequest request = new UserChangePasswordRequest("12345678", "QWER1234");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).thenReturn(false);
        when(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).thenReturn(false);

        // When & Then
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> userService.changePassword(1L, request));
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }
}