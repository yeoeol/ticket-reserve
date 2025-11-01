package ticket.reserve.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.inventory.dto.InventoryRequestDto;
import ticket.reserve.inventory.dto.InventoryResponseDto;
import ticket.reserve.inventory.repository.InventoryRepository;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public InventoryResponseDto createInventory(InventoryRequestDto request) {
        Inventory inventory = request.toEntity();
        return InventoryResponseDto.from(inventoryRepository.save(inventory));
    }

    @Transactional
    public InventoryResponseDto reserveSeats(Long eventId, int count) {
        Inventory inventory = inventoryRepository.findByEventIdForUpdate(eventId)
                .orElseThrow(() -> new RuntimeException("이벤트의 좌석 정보를 찾을 수 없습니다."));
        inventory.reserve(count);
        return InventoryResponseDto.from(inventory);
    }

    @Transactional
    public void releaseSeats(Long eventId, int count) {
        Inventory inventory = inventoryRepository.findByEventIdForUpdate(eventId)
                .orElseThrow(() -> new RuntimeException("이벤트의 좌석 정보를 찾을 수 없습니다."));
        inventory.release(count);
    }

    @Transactional(readOnly = true)
    public InventoryResponseDto getInventory(Long eventId) {
        return inventoryRepository.findByEventId(eventId)
                .map(InventoryResponseDto::from)
                .orElseThrow(() -> new RuntimeException("이벤트의 좌석 정보를 찾을 수 없습니다."));
    }
}
