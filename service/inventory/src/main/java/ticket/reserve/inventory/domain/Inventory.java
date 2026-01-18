package ticket.reserve.inventory.domain;

import jakarta.persistence.*;
import lombok.*;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.inventory.domain.enums.InventoryStatus;
import ticket.reserve.tsid.IdGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "inventories")
public class Inventory extends BaseTimeEntity {

    @Id
    @Column(name = "inventory_id")
    private Long id;

    private Long buskingId;

    private String inventoryName;

    private Integer price;

    @Enumerated(EnumType.STRING)
    private InventoryStatus status;

    @Builder(access = AccessLevel.PRIVATE)
    private Inventory(IdGenerator idGenerator, Long buskingId, String inventoryName, Integer price) {
        this.id = idGenerator.nextId();
        this.buskingId = buskingId;
        this.inventoryName = inventoryName;
        this.price = price;
        this.status = InventoryStatus.AVAILABLE;
    }

    public static Inventory create(IdGenerator idGenerator, Long buskingId, String inventoryName, Integer price) {
        return Inventory.builder()
                .idGenerator(idGenerator)
                .buskingId(buskingId)
                .inventoryName(inventoryName)
                .price(price)
                .build();
    }

    public void hold() {
        if (this.status != InventoryStatus.AVAILABLE) {
            throw new CustomException(ErrorCode.INVENTORY_HOLD_FAIL);
        }
        this.status = InventoryStatus.PENDING;
    }

    public void confirm() {
        if (this.status != InventoryStatus.PENDING) {
            throw new CustomException(ErrorCode.INVENTORY_CONFIRM_FAIL);
        }
        this.status = InventoryStatus.OCCUPIED;
    }

    public void release() {
        if (this.status != InventoryStatus.PENDING) {
            throw new CustomException(ErrorCode.INVENTORY_RELEASE_FAIL);
        }
        this.status = InventoryStatus.AVAILABLE;
    }

    public void update(String inventoryName, Integer price) {
        this.inventoryName = inventoryName;
        this.price = price;
    }
}
