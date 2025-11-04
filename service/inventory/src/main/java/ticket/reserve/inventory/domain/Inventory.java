package ticket.reserve.inventory.domain;

import jakarta.persistence.*;
import lombok.*;
import ticket.reserve.inventory.domain.enums.InventoryStatus;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "inventories")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    private Long eventId;

    private int price;

    @Enumerated(EnumType.STRING)
    private InventoryStatus status;

    private int totalSeats;
    private int availableSeats;

    /**
     * 좌석 잠금 (예약 시도)
     */
    public void reserve(int count) {
        if (availableSeats < count) {
            throw new RuntimeException("좌석이 부족합니다.");
        }
        this.availableSeats -= count;
    }

    /**
     * 좌석 해제 (결제 실패 등)
     */
    public void release(int count) {
        this.availableSeats += count;
        if (availableSeats > totalSeats) {
            availableSeats = totalSeats;
        }
    }

    public void release() {
        this.status = InventoryStatus.AVAILABLE;
    }

    public void hold() {
        this.status = InventoryStatus.PENDING;
    }

    public void confirm() {
        this.status = InventoryStatus.OCCUPIED;
    }
}
