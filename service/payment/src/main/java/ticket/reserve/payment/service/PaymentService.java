package ticket.reserve.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.payment.domain.Payment;
import ticket.reserve.payment.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public void createPayment(String orderId, Long userId, Long reservationId) {
        Payment payment = Payment.builder()
                .userId(userId)
                .reservationId(reservationId)
                .orderId(orderId)
                .build();

        paymentRepository.save(payment);
    }
}
