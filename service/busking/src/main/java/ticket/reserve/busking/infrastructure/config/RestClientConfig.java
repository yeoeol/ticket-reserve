package ticket.reserve.busking.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ticket.reserve.busking.infrastructure.client.interceptor.AuthenticationInterceptor;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final RestClient.Builder restClientBuilder;
    private final AuthenticationInterceptor authenticationInterceptor;

    @Value("${endpoints.ticket-reserve-subscription-service.url}")
    private String subscriptionServiceUrl;

    @Value("${endpoints.ticket-reserve-image-service.url}")
    private String imageServiceUrl;

    @Bean
    public RestClient subscriptionRestClient() {
        return restClientBuilder
                .baseUrl(subscriptionServiceUrl)
                .requestInterceptor(authenticationInterceptor)
                .build();
    }

    @Bean
    public RestClient imageRestClient() {
        return restClientBuilder
                .baseUrl(imageServiceUrl)
                .requestInterceptor(authenticationInterceptor)
                .build();
    }
}
