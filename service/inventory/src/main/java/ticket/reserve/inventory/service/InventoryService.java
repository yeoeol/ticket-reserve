package ticket.reserve.inventory.service;

import lombok.RequiredArgsConstructor;
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

    @Transactional
    public void createInventory(InventoryRequestDto request) {
        EventDetailResponseDto eventResponseDto = eventServiceClient.getOne(request.eventId());
        Integer eventInventoryCount = inventoryRepository.countInventoryByEventId(request.eventId());
        if (eventResponseDto.totalSeats() == eventInventoryCount) {
            throw new RuntimeException("해당 이벤트에 더이상 좌석을 생성할 수 없습니다.");
        }

        Inventory inventory = request.toEntity();
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void updateInventory(Long inventoryId, InventoryUpdateRequestDto request) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("발견된 좌석이 없습니다."));
        inventory.update(request.inventoryName(), request.price());
    }

    @Transactional(readOnly = true)
    public InventoryListResponseDto getInventories(Long eventId) {
        EventDetailResponseDto responseDto = eventServiceClient.getOne(eventId);
        List<Inventory> inventoryList = inventoryRepository.findAllByEventId(eventId);

        List<InventoryResponseDto> responseDtoList = inventoryList.stream()
                .map(InventoryResponseDto::from)
                .toList();

        return InventoryListResponseDto.of(responseDto, responseDtoList);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponseDto> getInventoryList(Long eventId) {
        List<Inventory> inventoryList = inventoryRepository.findAllByEventId(eventId);
        return inventoryList.stream()
                .map(InventoryResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public InventoryResponseDto getInventory(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("발견된 좌석이 없습니다."));

        return InventoryResponseDto.from(inventory);
    }

    // 좌석 선점 로직
    @Transactional
    public void holdInventory(Long inventoryId) {
        Inventory inventory = inventoryRepository.findByEventIdForUpdate(inventoryId)
                .orElseThrow(() -> new RuntimeException("이벤트의 좌석 정보를 찾을 수 없습니다."));
        inventory.hold();
    }

    @Transactional
    public void confirmInventory(Long inventoryId) {
        Inventory inventory = inventoryRepository.findByEventIdForUpdate(inventoryId)
                .orElseThrow(() -> new RuntimeException("이벤트의 좌석 정보를 찾을 수 없습니다."));
        inventory.confirm();
    }

    @Transactional
    public void releaseInventory(Long inventoryId) {
        Inventory inventory = inventoryRepository.findByEventIdForUpdate(inventoryId)
                .orElseThrow(() -> new RuntimeException("이벤트의 좌석 정보를 찾을 수 없습니다."));
        inventory.release();
    }

    @Transactional(readOnly = true)
    public Integer getAvailableInventoryCounts(Long eventId) {
        return inventoryRepository.countAvailableInventoryByEventId(eventId);
    }

    @Transactional
    public void deleteInventory(Long inventoryId) {
        inventoryRepository.deleteById(inventoryId);
    }
}
