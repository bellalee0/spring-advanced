package org.example.expert.fixtures;

import org.example.expert.common.utils.PasswordEncoder;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;

public class UserFixture {

    static PasswordEncoder passwordEncoder = new PasswordEncoder();

    public static String DEFAULT_EMAIL = "test@test.com";
    public static String DEFAULT_PASSWORD = "1234";
    public static String INCODED_PASSWORD = passwordEncoder.encode(DEFAULT_PASSWORD);

    public static User createTestUserAdminRole() {
        return new User(DEFAULT_EMAIL, INCODED_PASSWORD, UserRole.ADMIN);
    }

    public static User createTestUserUserRole() {
        return new User(DEFAULT_EMAIL, INCODED_PASSWORD, UserRole.USER);
    }
}
