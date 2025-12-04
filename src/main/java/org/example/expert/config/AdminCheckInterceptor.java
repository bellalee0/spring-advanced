package org.example.expert.config;

import static org.example.expert.domain.user.enums.UserRole.ADMIN;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j(topic = "AdminCheckInterceptor")
@Component
@RequiredArgsConstructor
public class AdminCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String path = request.getRequestURI();
        LocalDateTime requestTime = LocalDateTime.now();

        UserRole userRole = UserRole.of(request.getAttribute("userRole").toString());

        if (!ADMIN.equals(userRole)) {
            throw new InvalidRequestException("관리자만 접근 가능한 페이지입니다.");
        }

        log.info("관리자 페이지 접근: requestTime={}, path={}", requestTime, path);
        
        return true;
    }
}
