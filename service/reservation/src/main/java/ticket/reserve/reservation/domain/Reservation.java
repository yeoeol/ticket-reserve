package ticket.reserve.reservation.domain;

import jakarta.persistence.*;
import lombok.*;
import ticket.reserve.reservation.domain.enums.ReservationStatus;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "reservations")
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;
    private Long eventId;
    private Long inventoryId;
    private int price;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public void release() {
        this.status = ReservationStatus.CANCELLED;
    }
}
