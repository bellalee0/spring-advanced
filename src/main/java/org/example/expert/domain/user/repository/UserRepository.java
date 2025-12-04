package org.example.expert.domain.user.repository;

import org.example.expert.common.dto.AuthUser;
import org.example.expert.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    default User fromAuthUser(AuthUser authUser) {
        return findById(authUser.getId())
            .orElseThrow(() -> new InvalidRequestException("등록된 유저가 아닙니다."));
    }
}
