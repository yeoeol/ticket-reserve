package ticket.reserve.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.inventory.dto.InventoryRequestDto;
import ticket.reserve.inventory.dto.InventoryResponseDto;
import ticket.reserve.inventory.repository.InventoryRepository;

import java.util.List;

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
    public InventoryResponseDto reserveSeats(Long eventId, Long inventoryId) {
        Inventory inventory = inventoryRepository.findByEventIdForUpdate(eventId, inventoryId)
                .orElseThrow(() -> new RuntimeException("이벤트의 좌석 정보를 찾을 수 없습니다."));
        inventory.reserve(1);
        return InventoryResponseDto.from(inventory);
    }

    @Transactional
    public void releaseSeats(Long eventId, Long inventoryId) {
        Inventory inventory = inventoryRepository.findByEventIdForUpdate(eventId, inventoryId)
                .orElseThrow(() -> new RuntimeException("이벤트의 좌석 정보를 찾을 수 없습니다."));
        inventory.release(1);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponseDto> getInventoryList(Long eventId) {
        return inventoryRepository.findAllByEventId(eventId)
                .stream()
                .map(InventoryResponseDto::from)
                .toList();
    }
}
