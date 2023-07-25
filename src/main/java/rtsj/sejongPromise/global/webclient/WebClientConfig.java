package rtsj.sejongPromise.global.webclient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @Primary
    public WebClient plainWebClient() {
        // todo : connection pool 등 세부 값 조정하기.
        return WebClient.create();
    }

    @Bean
    @Qualifier("chromeAgentWebClient")
    public WebClient chromeAgentWebClient() {
        return WebClient.builder()
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                .build();
    }
}
