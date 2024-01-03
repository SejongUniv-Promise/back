package rtsj.sejongPromise.global.auth.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtProvider implements AuthenticationTokenProvider{
    private static final String AUTHORIZATION = "Authorization";

    @Value("${app.auth.jwt.access-expiration}")
    private final Duration accessExpiration;

    @Value("${app.auth.jwt.refresh-expiration}")
    private final Duration refreshExpiration;

    @Value("${app.auth.jwt.secret-key}")
    private final String secretKey;

    @Override
    public AuthenticationToken issue(Long userId, String role) {
        return JwtAuthenticationToken.builder()
                .accessToken(createAccessToken(userId, role))
                .refreshToken(createRefreshToken())
                .build();
    }

    @Override
    public AuthenticationToken reissue(String accessToken, String refreshToken) {
        //만료되면 새로운 refreshToken 반환.
        String validateRefreshToken = validateRefreshToken(refreshToken);
        accessToken = refreshAccessToken(accessToken);

        return JwtAuthenticationToken.builder()
                .accessToken(accessToken)
                .refreshToken(validateRefreshToken)
                .build();
    }

    @Override
    public Authentication getAuthentication(String accessToken) {
        Jws<Claims> claimsJws = validateAccessToken(accessToken);

        Claims body = claimsJws.getBody();
        Long userId = Long.parseLong((String) body.get("userId"));
        String userRole = (String) body.get("userRole");

        return new JwtAuthentication(userId, userRole);
    }

    @Override
    public String getAccessTokenFromHeader(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase("access-token")) {
                    return cookie.getValue();
                }
            }
        }
        String header = request.getHeader(AUTHORIZATION);
        if (header != null) {
            if (!header.toLowerCase().startsWith("bearer ")) {
                throw new RuntimeException();
            }
            return header.substring(7);
        }
        return null;
    }

    private String createAccessToken(Long userId, String role) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validity = now.plus(accessExpiration);

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("userId", userId);
        payloads.put("userRole", role);

        return Jwts.builder()
                .setSubject("userInfo") //"sub":"userInfo"
                .setClaims(payloads)
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(validity.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    private String refreshAccessToken(String accessToken) {
        Long userId;
        String role;
        try {
            Jws<Claims> claimsJws = validateAccessToken(accessToken);
            Claims body = claimsJws.getBody();
            userId = (Long) body.get("userId");
            role = (String) body.get("userRole");
        } catch (ExpiredJwtException e) {
            userId = (Long) e.getClaims().get("userId");
            role = (String) e.getClaims().get("userRole");
        }
        return createAccessToken(userId, role);
    }

    private String createRefreshToken() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validity = now.plus(refreshExpiration);
        return Jwts.builder()
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(validity.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    private Jws<Claims> validateAccessToken(String accessToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(accessToken);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException();
        } catch (JwtException e) {
            throw new IllegalStateException();
        }
    }

    private String validateRefreshToken(String refreshToken) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(refreshToken);
            return refreshToken;
        } catch (ExpiredJwtException e) {
            return createRefreshToken();
        } catch (JwtException e) {
            throw new RuntimeException();
        }
    }
}
