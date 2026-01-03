package ticket.reserve.payment.application.port.out;

public interface PaymentPropertiesPort {
    String getClientKey();
    String getSecretKey();
}
