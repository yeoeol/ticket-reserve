package ticket.reserve.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ticket.reserve.inventory.domain.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
