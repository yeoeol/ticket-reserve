package ticket.reserve.reservation.domain;

import jakarta.persistence.*;
import lombok.*;
import ticket.reserve.reservation.domain.enums.ReservationStatus;
import ticket.reserve.tsid.IdGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservations")
public class Reservation extends BaseTimeEntity {

    @Id
    @Column(name = "reservation_id")
    private Long id;

    private Long userId;
    private Long eventId;
    private Long inventoryId;
    private Integer price;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Builder(access = AccessLevel.PRIVATE)
    private Reservation(IdGenerator idGenerator, Long userId, Long eventId, Long inventoryId, Integer price) {
        this.id = idGenerator.nextId();
        this.userId = userId;
        this.eventId = eventId;
        this.inventoryId = inventoryId;
        this.price = price;
        this.status = ReservationStatus.PENDING;
    }

    public static Reservation create(IdGenerator idGenerator, Long userId, Long eventId, Long inventoryId, Integer price) {
        return Reservation.builder()
                .idGenerator(idGenerator)
                .userId(userId)
                .eventId(eventId)
                .inventoryId(inventoryId)
                .price(price)
                .build();
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public void release() {
        this.status = ReservationStatus.CANCELLED;
    }
}
