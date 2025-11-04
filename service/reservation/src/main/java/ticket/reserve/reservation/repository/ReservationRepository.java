package ticket.reserve.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.reservation.domain.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
