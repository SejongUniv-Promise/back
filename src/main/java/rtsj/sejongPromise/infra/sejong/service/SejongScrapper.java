package rtsj.sejongPromise.infra.sejong.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import rtsj.sejongPromise.infra.sejong.model.SejongAuth;

@RequiredArgsConstructor
public class SejongScrapper {

    private final WebClient webClient;

    /**
     * 인증이 필요하지 않은 요청은 Html 을 반환합니다.
     * @param uri
     * @return
     */
    protected String requestWebInfo(String uri){
        String result;
        try{
            result = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }
        return result;
    }

    protected String requestWebInfo(SejongAuth auth, String uri){
        String result;
        try{
            result = webClient.get()
                    .uri(uri)
                    .cookies(auth.authCookies())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }

        if(result == null){
            throw new RuntimeException("응답값이 존재하지 않습니다.");
        }
        return result;
    }

}
