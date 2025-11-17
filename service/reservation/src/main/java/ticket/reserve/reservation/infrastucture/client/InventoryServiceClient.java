package ticket.reserve.reservation.infrastucture.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ticket.reserve.reservation.application.dto.request.InventoryHoldRequestDto;
import ticket.reserve.reservation.application.port.out.InventoryPort;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryServiceClient extends InventoryPort {

    /**
     * inventory-service의 /api/inventory/hold (좌석 임시 선점) API를 호출
     */
    @Override
    @PostMapping("/api/inventory/hold")
    void holdInventory(@RequestBody InventoryHoldRequestDto request);
}
