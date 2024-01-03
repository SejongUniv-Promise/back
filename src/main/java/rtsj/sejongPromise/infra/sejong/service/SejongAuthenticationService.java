package rtsj.sejongPromise.infra.sejong.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import rtsj.sejongPromise.global.webclient.ChromeAgentWebclient;
import rtsj.sejongPromise.infra.sejong.model.SejongAuth;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SejongAuthenticationService {
    private static final Pattern ERROR_ALERT_PATTERN = Pattern.compile("<p\\s*class=\"tc\">\\s*(.*)\\s*</p>");
    @Value("${sejong.student.login}")
    private final String LOGIN_URI;

    @ChromeAgentWebclient
    private final WebClient webClient;


    public SejongAuth getSejongAuth(String studentId, String password){
        String param = makeParam("userId=%s&password=%s&go=", studentId, password);
        return login(LOGIN_URI, param, "http://classic.sejong.ac.kr", "http://classic.sejong.ac.kr/userLoginPage.do");
    }


    private SejongAuth login(String uri, String param, String origin, String referer) {
        MultiValueMap<String, String> cookies = new LinkedMultiValueMap<>();

        // 로그인 시도 -> 302 redirect
        ResponseEntity<String> response = tryLogin(uri, param, origin, referer);
        HttpHeaders headers = response.getHeaders();

        //Set-Cookie
        addMappedCookies(cookies, headers);

        return new SejongAuth(cookies);
    }

    private void addMappedCookies(MultiValueMap<String, String> dest, HttpHeaders src) {
        List<String> cookieStrings = src.get(HttpHeaders.SET_COOKIE);
        List<ResponseCookie> cookies = new ArrayList<>();
        for(String cookieString : cookieStrings){
            String[] block = cookieString.split(";");
            String[] cookieData = block[0].split("=");
            ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(cookieData[0], cookieData[1]);
            for(int i=1; i<block.length; i++){
                builder = addCookieProperties(builder, block[i]);
            }

            cookies.add(builder.build());
        }

        for(ResponseCookie cookie : cookies){
            dest.add(cookie.getName(), cookie.getValue());
        }
    }

    private ResponseCookie.ResponseCookieBuilder addCookieProperties(ResponseCookie.ResponseCookieBuilder builder, String properties) {
        String block[] = properties.split("=");
        if(block.length == 1){
            String name = block[0].trim();
            if(name.equalsIgnoreCase("httponly")) {
                builder.httpOnly(true);
            }else if(name.equalsIgnoreCase("secure")){
                builder.secure(true);
            }
        }else if(block.length == 2){
            String name = block[0].trim();
            String value = block[1].trim();
            if(name.equalsIgnoreCase("domain")) {
                builder.domain(value);
            }else if(name.equalsIgnoreCase("path")){
                builder.path(value);
            }else if(name.equalsIgnoreCase("max-age")){
                builder.maxAge(Long.parseLong(value));
            }
        }
        return builder;
    }

    private ResponseEntity<String> tryLogin(String uri, String param, String origin, String referer) {
        ResponseEntity<String> response;
        try{
            response = webClient.post()
                    .uri(uri)
                    .header("Origin", origin)
                    .header("Referer", referer)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(param)
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException("로그인 시도 중 에러가 발생했습니다.");
        }
        validateResponse(response);
        validateStatusCode(response);
        return response;
    }

    private void validateStatusCode(ResponseEntity<String> response) {
        if(response.getStatusCode().is2xxSuccessful()){
            throwLoginFailedException(response);
            return;
        }
        if(!response.getStatusCode().is3xxRedirection()){
            throw new ResponseStatusException(response.getStatusCode());
        }
    }

    private void validateResponse(ResponseEntity<String> response) {
        if(response == null){
            throw new RuntimeException("응답값 존재하지 않음");
        }
    }

    private void throwLoginFailedException(ResponseEntity<String> response) {
        String responseBody = response.getBody();
        if(responseBody == null){
            throw new RuntimeException("에러 응답 존재하지 않음");
        }
        String errorMessage = extractErrorMessage(responseBody);
        if(errorMessage == null){
            throw new RuntimeException("에러 메시지 존재하지 않음");
        }
        throw new RuntimeException(errorMessage);
    }

    private String extractErrorMessage(String responseBody) {
        Matcher matcher = ERROR_ALERT_PATTERN.matcher(responseBody);
        if(matcher.find()){
            return matcher.group(1);
        }
        return null;
    }


    private String makeParam(String format, String... args){
        String[] encodedParam = new String[args.length];
        for(int i=0; i<args.length; i++){
            encodedParam[i] = URLEncoder.encode(args[i], StandardCharsets.UTF_8);
        }
        return String.format(format, encodedParam);
    }

}
