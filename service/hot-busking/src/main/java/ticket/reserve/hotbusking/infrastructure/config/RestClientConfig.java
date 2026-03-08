package ticket.reserve.hotbusking.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ticket.reserve.hotbusking.infrastructure.client.interceptor.AuthenticationInterceptor;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final RestClient.Builder restClientBuilder;
    private final AuthenticationInterceptor authenticationInterceptor;

    @Value("${endpoints.ticket-reserve-busking-service.url}")
    private String buskingServiceUrl;

    @Bean
    public RestClient.Builder restClientBuilder(
            AuthenticationInterceptor authenticationInterceptor
    ) {
        return RestClient.builder()
                .requestInterceptor(authenticationInterceptor);
    }

    @Bean
    public RestClient buskingRestClient() {
        return restClientBuilder
                .baseUrl(buskingServiceUrl)
                .requestInterceptor(authenticationInterceptor)
                .build();
    }
}
