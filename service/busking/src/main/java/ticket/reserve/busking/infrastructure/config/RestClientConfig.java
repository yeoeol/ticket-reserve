package ticket.reserve.busking.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ticket.reserve.busking.infrastructure.client.interceptor.AuthenticationInterceptor;
import ticket.reserve.core.log.interceptor.RestClientTraceInterceptor;

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
