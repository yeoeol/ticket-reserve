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
public class Inventory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    private Long eventId;

    private String inventoryName;

    private int price;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InventoryStatus status = InventoryStatus.AVAILABLE;

    public void hold() {
        if (this.status != InventoryStatus.AVAILABLE) {
            throw new RuntimeException("좌석 ID : " + this.id + " - 이미 선택된 좌석입니다.");
        }
        this.status = InventoryStatus.PENDING;
    }

    public void confirm() {
        if (this.status != InventoryStatus.PENDING) {
            System.out.println("비정상적인 확정 시도");
            return;
        }
        this.status = InventoryStatus.OCCUPIED;
    }

    public void release() {
        if (this.status != InventoryStatus.PENDING) {
            System.out.println("선점 상태가 아닌 좌석의 릴리즈 시도");
            return;
        }
        this.status = InventoryStatus.AVAILABLE;
    }

    public void update(String inventoryName, int price) {
        this.inventoryName = inventoryName;
        this.price = price;
    }
}
