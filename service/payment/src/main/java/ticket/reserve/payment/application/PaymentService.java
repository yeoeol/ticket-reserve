package ticket.reserve.payment.application;

import ticket.reserve.payment.application.dto.request.PaymentConfirmRequestDto;

public interface PaymentService {
    /**
     * Payment 엔티티 생성
     */
    void createPayment(String orderId, Long userId, Long reservationId, Long inventoryId);

    /**
     * 결제 승인
     */
    void confirmPayment(PaymentConfirmRequestDto request);
}
