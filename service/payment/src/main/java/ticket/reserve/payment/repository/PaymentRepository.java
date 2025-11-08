package ticket.reserve.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.payment.domain.Payment;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(String orderId);
}
