package org.example.expert.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.fixtures.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAdminService userAdminService;


    @Test
    @DisplayName("유저 권한 변경 테스트 - 실제 DB 변경 검증")
    void changeUserRole_success() {

        // Given
        User user = UserFixture.createTestUserUserRole();
        ReflectionTestUtils.setField(user, "id", 1L);

        long userId = user.getId();
        UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest(UserRole.ADMIN.toString());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userAdminService.changeUserRole(userId, userRoleChangeRequest);

        // Then
        User changedUser = userRepository.findById(userId).orElseThrow();
        assertThat(changedUser.getUserRole()).isEqualTo(UserRole.ADMIN);
    }
}