package ticket.reserve.payment.application.port.out;

import ticket.reserve.common.event.payload.PaymentConfirmedEventPayload;

public interface PaymentPublishPort {
    void paymentConfirmEvent(PaymentConfirmedEventPayload payload);
}
