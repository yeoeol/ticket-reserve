package ticket.reserve.admin.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ticket.reserve.admin.global.aop.interceptor.AuthenticationInterceptor;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder(AuthenticationInterceptor interceptor) {
        return RestClient.builder()
                .requestInterceptor(interceptor);
    }
}
