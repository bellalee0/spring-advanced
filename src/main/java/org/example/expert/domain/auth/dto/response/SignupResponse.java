package org.example.expert.domain.auth.dto.response;

import lombok.Getter;
import org.example.expert.domain.user.enums.UserRole;

@Getter
public class SignupResponse {

    private final Long id;
    private final String email;
    private final UserRole role;

    public SignupResponse(Long id, String email, UserRole role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }
}
