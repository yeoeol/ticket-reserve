package ticket.reserve.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.payment.client.ReservationServiceClient;
import ticket.reserve.payment.client.TossPaymentsClient;
import ticket.reserve.payment.client.dto.TossResponseDto;
import ticket.reserve.payment.domain.Payment;
import ticket.reserve.payment.dto.PaymentConfirmRequestDto;
import ticket.reserve.payment.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossPaymentsClient tossPaymentsClient;
    private final ReservationServiceClient reservationServiceClient;

    @Transactional
    public void createPayment(String orderId, Long userId, Long reservationId) {
        Payment payment = Payment.builder()
                .userId(userId)
                .reservationId(reservationId)
                .orderId(orderId)
                .build();

        paymentRepository.save(payment);
    }

    @Transactional
    public void confirmPayment(PaymentConfirmRequestDto request) {
        TossResponseDto tossResponseDto = tossPaymentsClient.confirmPayment(request);

        Payment payment = paymentRepository.findByOrderId(tossResponseDto.orderId())
                .orElseThrow(() -> new RuntimeException("Payment NOT FOUND : By orderId"));
        payment.setting(tossResponseDto);

        // OpenFeign - Reservation-service의 confirm 호출
        reservationServiceClient.confirmReservation(payment.getReservationId());
    }
}
