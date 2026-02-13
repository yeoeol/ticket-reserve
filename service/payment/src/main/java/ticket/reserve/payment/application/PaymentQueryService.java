package ticket.reserve.payment.application;

import ticket.reserve.payment.domain.Payment;

public interface PaymentQueryService {
    Payment findByOrderId(String orderId);
}
