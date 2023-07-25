package rtsj.sejongPromise.infra.sejong.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
public class SejongScrapper {

    private final WebClient webClient;

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
}
