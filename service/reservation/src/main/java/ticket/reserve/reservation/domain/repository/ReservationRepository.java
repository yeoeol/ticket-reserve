package ticket.reserve.reservation.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.reservation.domain.Reservation;
import ticket.reserve.reservation.domain.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByStatusAndCreatedAtBefore(ReservationStatus status, LocalDateTime expirationTime);
}
