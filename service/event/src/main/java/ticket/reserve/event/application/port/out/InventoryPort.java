package ticket.reserve.event.application.port.out;

public interface InventoryPort {
    Integer countsInventory(Long eventId);
}
