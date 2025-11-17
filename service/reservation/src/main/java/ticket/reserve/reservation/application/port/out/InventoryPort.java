package ticket.reserve.reservation.application.port.out;

import ticket.reserve.reservation.application.dto.request.InventoryHoldRequestDto;

public interface InventoryPort {
    void holdInventory(InventoryHoldRequestDto request);
}
