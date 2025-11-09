package ticket.reserve.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.inventory.client.EventServiceClient;
import ticket.reserve.inventory.client.dto.EventDetailResponseDto;
import ticket.reserve.inventory.domain.Inventory;
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
        EventDetailResponseDto eventResponseDto = eventServiceClient.getOne(request.eventId());
        Integer eventInventoryCount = inventoryRepository.countInventoryByEventId(request.eventId());
        if (eventResponseDto.totalSeats() == eventInventoryCount) {
            throw new RuntimeException("해당 이벤트에 더이상 좌석을 생성할 수 없습니다.");
        }

        Inventory inventory = request.toEntity();
        Inventory savedInventory = inventoryRepository.save(inventory);
        return InventoryCreateResponseDto.of(eventResponseDto, InventoryResponseDto.from(savedInventory));
    }

    @Transactional(readOnly = true)
    public InventoryListResponseDto getInventoryList(Long eventId) {
        EventDetailResponseDto responseDto = eventServiceClient.getOne(eventId);
        List<Inventory> inventoryList = inventoryRepository.findAllByEventId(eventId);

        List<InventoryResponseDto> responseDtoList = inventoryList.stream()
                .map(InventoryResponseDto::from)
                .toList();

        return InventoryListResponseDto.of(responseDto, responseDtoList);
    }

    // 좌석 선점 로직
    @Transactional
    public void holdInventory(InventoryHoldRequestDto request) {
        Inventory inventory = inventoryRepository.findByEventIdForUpdate(request.eventId(), request.inventoryId())
                .orElseThrow(() -> new RuntimeException("이벤트의 좌석 정보를 찾을 수 없습니다."));
        inventory.hold();
    }

    @Transactional
    public void confirmInventory(InventoryConfirmRequestDto request) {
        Inventory inventory = inventoryRepository.findByEventIdForUpdate(request.eventId(), request.inventoryId())
                .orElseThrow(() -> new RuntimeException("이벤트의 좌석 정보를 찾을 수 없습니다."));
        inventory.confirm();
    }

    @Transactional
    public void releaseInventory(InventoryReleaseRequestDto request) {
        Inventory inventory = inventoryRepository.findByEventIdForUpdate(request.eventId(), request.inventoryId())
                .orElseThrow(() -> new RuntimeException("이벤트의 좌석 정보를 찾을 수 없습니다."));
        inventory.release();
    }

    @Transactional(readOnly = true)
    public Integer getAvailableInventoryCounts(Long eventId) {
        return inventoryRepository.countAvailableInventoryByEventId(eventId);
    }
}
