package ticket.reserve.payment.infrastructure.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RequiredArgsConstructor
public class TossFeignConfig {

    private final PaymentProperties paymentProperties;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                String secretKey = paymentProperties.getSecretKey();
                String authHeader = "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

                template.header(HttpHeaders.AUTHORIZATION, authHeader);
            }
        };
    }
}
