package rtsj.sejongPromise.global.auth.jwt;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface AuthenticationTokenProvider {
    // userId & Role 을 기반으로 Authentication Token을 발급한다.
    AuthenticationToken issue(Long userId, String Role);
    // access & refresh token을 기반으로 Authentication Token을 재발급한다.
    AuthenticationToken reissue(String accessToken, String refreshToken);

    Authentication getAuthentication(String accessToken);

    //header에서 access token을 가져온다.
    String getAccessTokenFromHeader(HttpServletRequest request);
}
