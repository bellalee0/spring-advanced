package org.example.expert.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Aspect
@Component
@Slf4j(topic = "AdminCheckAop")
public class AdminCheckAop {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("execution(* org.example.expert.domain.user.service.UserAdminService.*(..)) || execution(* org.example.expert.domain.comment.service.CommentAdminService.*(..))")
    public Object executionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;

        String userId = request.getAttribute("userId").toString();
        String requestUrl = request.getRequestURL().toString();
        LocalDateTime requestTime = LocalDateTime.now();
        String requestBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);

        log.info("관리자 페이지 접근: userId={}, URI={}, requestTime={}, requestBody={}", userId, requestUrl, requestTime, requestBody);

        Object result = joinPoint.proceed();

        String responseBody = objectMapper.writeValueAsString(result);
        log.info("관리자 페이지 접근 종료: userId={}, URI={}, responseBody={}", userId, requestUrl, responseBody);

        return result;
    }
}
