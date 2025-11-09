package ticket.reserve.inventory.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import ticket.reserve.inventory.domain.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.eventId = :eventId AND i.id = :inventoryId")
    Optional<Inventory> findByEventIdForUpdate(Long eventId, Long inventoryId);

    List<Inventory> findAllByEventId(Long eventId);

    @Query("SELECT count(*) FROM Inventory i WHERE i.eventId = :eventId AND i.status = 'AVAILABLE'")
    Integer countAvailableInventoryByEventId(Long eventId);

    @Query("SELECT count(*) FROM Inventory i WHERE i.eventId = :eventId")
    Integer countInventoryByEventId(Long eventId);
}
