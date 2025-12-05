package org.example.expert.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.example.expert.common.exception.AuthException;
import org.example.expert.common.exception.InvalidRequestException;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.fixtures.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입 통합 테스트 - 실제 DB에 저장 및 조회 검증")
    void signup_success() {

        // Given
        String email = UserFixture.DEFAULT_EMAIL;
        String password = UserFixture.DEFAULT_PASSWORD;
        String role = UserRole.USER.toString();

        // When
        authService.signup(new SignupRequest(email, password, role));

        // Then
        List<User> saveUserList = userRepository.findAll();

        assertThat(saveUserList).hasSize(1);
        assertThat(saveUserList.get(0).getId()).isEqualTo(1L);
        assertThat(saveUserList.get(0).getEmail()).isEqualTo(email);
        assertThat(saveUserList.get(0).getUserRole()).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("회원가입 통합 테스트 - 존재하는 이메일로 계정 생성")
    void signup_failure() {

        // Given
        User user = UserFixture.createTestUserUserRole();
        userRepository.save(user);

        String email = UserFixture.DEFAULT_EMAIL;
        String password = UserFixture.DEFAULT_PASSWORD;
        String role = UserRole.USER.toString();

        // When & Then
        assertThatThrownBy(() -> authService.signup(new SignupRequest(email, password, role)))
            .isInstanceOf(InvalidRequestException.class)
            .hasMessageContaining("이미 존재하는 이메일입니다.");
    }

    @Test
    @DisplayName("로그인 통합 테스트 - 토큰 생성 검증")
    void signin_success() {

        // Given
        User user = UserFixture.createTestUserUserRole();
        userRepository.save(user);

        String email = UserFixture.DEFAULT_EMAIL;
        String password = UserFixture.DEFAULT_PASSWORD;

        // When
        SigninResponse result = authService.signin(new SigninRequest(email, password));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBearerToken()).isNotNull();
    }

    @Test
    @DisplayName("로그인 통합 테스트 - 존재하자 않는 계정으로 로그인")
    void signin_failure_NotCreatedUser() {

        // Given
        String email = UserFixture.DEFAULT_EMAIL;
        String password = UserFixture.DEFAULT_PASSWORD;

        // When & Then
        assertThatThrownBy(() -> authService.signin(new SigninRequest(email, password)))
            .isInstanceOf(InvalidRequestException.class)
            .hasMessageContaining("가입되지 않은 유저입니다.");
    }

    @Test
    @DisplayName("로그인 통합 테스트 - 잘못된 비밀번호로 로그인")
    void signin_failure_WrongPassword() {

        // Given
        User user = UserFixture.createTestUserUserRole();
        userRepository.save(user);

        String email = UserFixture.DEFAULT_EMAIL;
        String password = "12345678";

        // When & Then
        assertThatThrownBy(() -> authService.signin(new SigninRequest(email, password)))
            .isInstanceOf(AuthException.class)
            .hasMessageContaining("잘못된 비밀번호입니다.");
    }
}