package ticket.reserve.payment.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ticket.reserve.payment.application.dto.request.PaymentConfirmRequestDto;
import ticket.reserve.payment.application.dto.response.TossResponseDto;
import ticket.reserve.payment.application.port.out.PaymentPropertiesPort;
import ticket.reserve.payment.application.port.out.TossPaymentsPort;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class TossPaymentsRestClientAdapter implements TossPaymentsPort {

    private final RestClient restClient;
    private final PaymentPropertiesPort paymentPropertiesPort;

    public TossPaymentsRestClientAdapter(
            @Value("${payment.base-url}") String baseUrl,
            PaymentPropertiesPort paymentPropertiesPort
    ) {
        this.restClient = RestClient.create(baseUrl);
        this.paymentPropertiesPort = paymentPropertiesPort;
    }

    @Override
    public TossResponseDto confirmPayment(PaymentConfirmRequestDto request) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .header(HttpHeaders.AUTHORIZATION, getAuthHeader())
                .body(request)
                .retrieve()
                .body(TossResponseDto.class);
    }

    private String getAuthHeader() {
        String secretKey = paymentPropertiesPort.getSecretKey();
        return "Basic " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }
}
