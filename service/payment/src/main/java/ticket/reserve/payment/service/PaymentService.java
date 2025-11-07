package ticket.reserve.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    public void createPayment(String orderId, Long userId, Long reservationId, int amount) {

    }
}
