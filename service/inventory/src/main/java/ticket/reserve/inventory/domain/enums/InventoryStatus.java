package ticket.reserve.inventory.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InventoryStatus {
    AVAILABLE("선택 가능"),
    PENDING("예매 진행 중"),
    OCCUPIED("선택 불가"),
    SELECTED("선택됨")
    ;

    private final String description;
}
