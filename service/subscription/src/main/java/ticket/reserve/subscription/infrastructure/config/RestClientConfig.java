package ticket.reserve.subscription.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ticket.reserve.subscription.infrastructure.client.interceptor.AuthenticationInterceptor;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder(AuthenticationInterceptor interceptor) {
        return RestClient.builder()
                .requestInterceptor(interceptor);
    }
}
