package ticket.reserve.admin.client.inventory.dto;

public record InventoryDetailResponseDto(
        Long inventoryId,
        String eventTitle,
        int price,
        String status
) {
    public InventoryDetailResponseDto() {
        this(null, "", 0, "");
    }
}
