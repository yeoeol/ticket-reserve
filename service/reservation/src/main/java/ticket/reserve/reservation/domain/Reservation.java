package ticket.reserve.reservation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import ticket.reserve.reservation.domain.enums.ReservationStatus;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;
    private Long eventId;
    private Long inventoryId;
    private int price;
    private ReservationStatus status;
}
