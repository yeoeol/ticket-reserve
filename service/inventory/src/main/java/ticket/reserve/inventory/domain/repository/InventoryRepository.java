package ticket.reserve.inventory.domain.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import ticket.reserve.inventory.domain.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.id = :inventoryId")
    Optional<Inventory> findByIdForUpdate(Long inventoryId);

    List<Inventory> findAllByBuskingId(Long buskingId);

    Page<Inventory> findAllByBuskingId(Long buskingId, Pageable pageable);

    @Query("SELECT count(*) FROM Inventory i WHERE i.buskingId = :buskingId AND i.status = 'AVAILABLE'")
    Integer countAvailableInventoryByBuskingId(Long buskingId);

    @Query("SELECT count(*) FROM Inventory i WHERE i.buskingId = :buskingId")
    Integer countInventoryByBuskingId(Long buskingId);

    Optional<Inventory> findByIdAndBuskingId(Long inventoryId, Long buskingId);
}
