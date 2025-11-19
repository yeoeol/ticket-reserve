package ticket.reserve.inventory.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.inventory.application.dto.response.CustomPageResponse;
import ticket.reserve.inventory.global.annotation.DistributedLock;
import ticket.reserve.inventory.application.port.out.EventPort;
import ticket.reserve.inventory.application.dto.response.InventoryListResponseDto;
import ticket.reserve.inventory.application.dto.request.InventoryRequestDto;
import ticket.reserve.inventory.application.dto.response.InventoryResponseDto;
import ticket.reserve.inventory.application.dto.request.InventoryUpdateRequestDto;
import ticket.reserve.inventory.application.dto.response.EventDetailResponseDto;
import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.inventory.domain.repository.InventoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final EventPort eventPort;

    @Transactional
    public void createInventory(InventoryRequestDto request) {
        EventDetailResponseDto eventResponseDto = eventPort.getOne(request.eventId());
        Integer eventInventoryCount = inventoryRepository.countInventoryByEventId(request.eventId());
        if (eventResponseDto.totalSeats() == eventInventoryCount) {
            throw new CustomException(ErrorCode.INVENTORY_EXCEED);
        }

        Inventory inventory = request.toEntity();
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void updateInventory(Long inventoryId, InventoryUpdateRequestDto request) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
        inventory.update(request.inventoryName(), request.price());
    }

    @Transactional(readOnly = true)
    public InventoryListResponseDto getInventories(Long eventId) {
        EventDetailResponseDto responseDto = eventPort.getOne(eventId);
        List<Inventory> inventoryList = inventoryRepository.findAllByEventId(eventId);

        List<InventoryResponseDto> responseDtoList = inventoryList.stream()
                .map(InventoryResponseDto::from)
                .toList();

        return InventoryListResponseDto.of(responseDto, responseDtoList);
    }

    @Transactional(readOnly = true)
    public CustomPageResponse<InventoryResponseDto> getInventoryPaging(Long eventId, Pageable pageable) {
        Page<Inventory> inventoryPage = inventoryRepository.findAllByEventId(eventId, pageable);
        Page<InventoryResponseDto> inventoryPageResponseDto = inventoryPage.map(InventoryResponseDto::from);

        return CustomPageResponse.from(inventoryPageResponseDto);
    }

    @Transactional(readOnly = true)
    public InventoryResponseDto getInventory(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

        return InventoryResponseDto.from(inventory);
    }

    // 락 미적용
    @Transactional
    public void holdInventoryV1(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
        inventory.hold();
    }

    // DB 수준 비관적 락 적용
    @Transactional
    public void holdInventoryV2(Long inventoryId) {
        Inventory inventory = inventoryRepository.findByIdForUpdate(inventoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
        inventory.hold();
    }

    // Redisson 분산 락 적용
    @DistributedLock(key = "'INVENTORY_LOCK:' + #inventoryId")
    public void holdInventory(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
        inventory.hold();
    }

    @Transactional
    public void confirmInventory(Long inventoryId) {
        Inventory inventory = inventoryRepository.findByIdForUpdate(inventoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
        inventory.confirm();
    }

    @Transactional
    public void releaseInventory(Long inventoryId) {
        Inventory inventory = inventoryRepository.findByIdForUpdate(inventoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
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

    @Transactional
    public void createInventoryAsTotalSeats(Long eventId, int totalSeats) {
        char prefix = 'A';
        int seatNumber = 1;

        for (int i = 1; i <= totalSeats; i++) {
            String inventoryName = prefix + String.valueOf(seatNumber);
            Inventory inventory = Inventory.builder()
                    .eventId(eventId)
                    .inventoryName(inventoryName)
                    .price(1000*i)
                    .build();
            inventoryRepository.save(inventory);
            seatNumber++;

            if (seatNumber > 10) {
                prefix++;
                seatNumber = 1;
            }
        }
    }
}
