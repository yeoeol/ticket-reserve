package ticket.reserve.inventory.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ticket.reserve.inventory.infrastructure.client.interceptor.AuthenticationInterceptor;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder(
            AuthenticationInterceptor authenticationInterceptor,
            RestClientTraceInterceptor restClientTraceInterceptor
    ) {
        return RestClient.builder()
                .requestInterceptor(authenticationInterceptor)
                .requestInterceptor(restClientTraceInterceptor);
    }
}
