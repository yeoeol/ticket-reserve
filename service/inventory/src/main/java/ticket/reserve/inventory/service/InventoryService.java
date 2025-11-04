package ticket.reserve.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.inventory.client.EventServiceClient;
import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.inventory.domain.enums.InventoryStatus;
import ticket.reserve.inventory.dto.*;
import ticket.reserve.inventory.repository.InventoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final EventServiceClient eventServiceClient;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public InventoryCreateResponseDto createInventory(InventoryRequestDto request) {
        Inventory inventory = request.toEntity();
        EventResponseDto eventResponseDto = eventServiceClient.getOne(request.eventId());

        Inventory savedInventory = inventoryRepository.save(inventory);
        return InventoryCreateResponseDto.of(eventResponseDto, InventoryResponseDto.from(savedInventory));
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
    public InventoryListResponseDto getInventoryList(Long eventId) {
        EventResponseDto responseDto = eventServiceClient.getOne(eventId);
        List<Inventory> inventoryList = inventoryRepository.findAllByEventId(eventId);

        List<InventoryResponseDto> responseDtoList = inventoryList.stream()
                .map(InventoryResponseDto::from)
                .toList();

        return InventoryListResponseDto.of(responseDto, responseDtoList);
    }
}
