package org.example.expert.domain.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.example.expert.common.dto.AuthUser;
import org.example.expert.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.fixtures.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("AuthUser에서 User 객체 변환 테스트 - 성공")
    void fromAuthUser_success() {

        // Given
        User user = UserFixture.createTestUserUserRole();
        userRepository.save(user);

        AuthUser authUser = new AuthUser(user.getId(), UserFixture.DEFAULT_EMAIL, UserRole.USER);

        // When
        User loggedinUser = userRepository.fromAuthUser(authUser);

        // Then
        assertEquals(user, loggedinUser);
    }

    @Test
    @DisplayName("AuthUser에서 User 객체 변환 테스트 - 실패: 존재하지 않는 User")
    void fromAuthUser_failure_NotFoundUser() {

        // Given
        AuthUser authUser = new AuthUser(1L, UserFixture.DEFAULT_EMAIL, UserRole.USER);

        // When & Then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
            () -> userRepository.fromAuthUser(authUser));
        assertEquals("등록된 유저가 아닙니다.", exception.getMessage());
    }
}