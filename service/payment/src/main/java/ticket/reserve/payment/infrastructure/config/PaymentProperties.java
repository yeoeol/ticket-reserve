package ticket.reserve.payment.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ticket.reserve.payment.application.port.out.PaymentPropertiesPort;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties implements PaymentPropertiesPort {

    private String clientKey;
    private String secretKey;
    private String baseUrl;
    private String confirmEndpoint;

    public String getConfirmUrl() {
        return baseUrl+confirmEndpoint;
    }
}
