package ticket.reserve.payment.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ticket.reserve.payment.application.dto.response.TossResponseDto;
import ticket.reserve.payment.application.port.out.TossPaymentsPort;
import ticket.reserve.payment.application.dto.request.PaymentConfirmRequestDto;
import ticket.reserve.payment.infrastructure.config.TossFeignConfig;

@FeignClient(name = "toss-payments-client", url = "${payment.base-url}", configuration = TossFeignConfig.class)
public interface TossPaymentsClient extends TossPaymentsPort {

    @PostMapping("/confirm") // 베이스 URL 뒤에 붙는 상세 경로
    TossResponseDto confirmPayment(@RequestBody PaymentConfirmRequestDto request);

}
