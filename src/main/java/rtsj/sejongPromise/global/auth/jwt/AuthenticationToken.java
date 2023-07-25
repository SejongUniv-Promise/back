package rtsj.sejongPromise.global.auth.jwt;

public interface AuthenticationToken {
    String getAccessToken();
    String getRefreshToken();
}
