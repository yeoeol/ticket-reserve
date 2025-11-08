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
