package ticket.reserve.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ticket.reserve.payment.client.dto.TossResponseDto;
import ticket.reserve.payment.config.TossFeignConfig;
import ticket.reserve.payment.dto.PaymentConfirmRequestDto;

@FeignClient(name = "toss-payments-client", url = "${payment.base-url}", configuration = TossFeignConfig.class)
public interface TossPaymentsClient {

    @PostMapping("/confirm") // 베이스 URL 뒤에 붙는 상세 경로
    TossResponseDto confirmPayment(@RequestBody PaymentConfirmRequestDto request);

}
