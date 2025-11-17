package ticket.reserve.event.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ticket.reserve.event.application.port.out.InventoryPort;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryServiceClient extends InventoryPort {

    @Override
    @GetMapping("/api/inventory/counts")
    Integer countsInventory(@RequestParam("eventId") Long eventId);
}
