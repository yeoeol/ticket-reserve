package ticket.reserve.payment.application.port.out;

import ticket.reserve.payment.application.dto.request.PaymentConfirmRequestDto;
import ticket.reserve.payment.application.dto.response.TossResponseDto;

public interface TossPaymentsPort {
    TossResponseDto confirmPayment(PaymentConfirmRequestDto request);
}
