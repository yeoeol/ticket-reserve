package ticket.reserve.inventory.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import ticket.reserve.inventory.domain.Inventory;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.eventId = :eventId")
    Optional<Inventory> findByEventIdForUpdate(Long eventId);

    Optional<Inventory> findByEventId(Long eventId);
}
