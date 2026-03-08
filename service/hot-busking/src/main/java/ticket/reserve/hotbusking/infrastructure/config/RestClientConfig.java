package ticket.reserve.hotbusking.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ticket.reserve.hotbusking.infrastructure.client.interceptor.AuthenticationInterceptor;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder(
            AuthenticationInterceptor authenticationInterceptor
    ) {
        return RestClient.builder()
                .requestInterceptor(authenticationInterceptor);
    }
}
