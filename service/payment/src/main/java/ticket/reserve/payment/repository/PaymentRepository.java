package ticket.reserve.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.payment.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
