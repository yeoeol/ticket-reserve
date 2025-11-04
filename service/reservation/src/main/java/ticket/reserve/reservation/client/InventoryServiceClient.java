package ticket.reserve.reservation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ticket.reserve.reservation.dto.InventoryConfirmRequestDto;
import ticket.reserve.reservation.dto.InventoryHoldRequestDto;
import ticket.reserve.reservation.dto.InventoryReleaseRequestDto;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryServiceClient {

    /**
     * inventory-service의 /api/inventory/hold (좌석 임시 선점) API를 호출
     */
    @PostMapping("/api/inventory/hold")
    void holdInventory(@RequestBody InventoryHoldRequestDto request);

    /**
     * inventory-service의 /api/inventory/confirm (좌석 확정) API를 호출
     */
    @PostMapping("/api/inventory/confirm")
    void confirmInventory(@RequestBody InventoryConfirmRequestDto request);

    /**
     * inventory-service의 /api/inventory/release (좌석 롤백) API를 호출
     */
    @PostMapping("/api/inventory/release")
    void releaseInventory(@RequestBody InventoryReleaseRequestDto request);
}
