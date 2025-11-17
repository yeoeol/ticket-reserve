package ticket.reserve.inventory.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        EventDetailResponseDto responseDto = eventPort.getOne(eventId);
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

    // 락 미적용
    @Transactional
    public void holdInventoryV1(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("이벤트의 좌석 정보를 찾을 수 없습니다."));
        inventory.hold();
    }

    // DB 수준 비관적 락 적용
    @Transactional
    public void holdInventoryV2(Long inventoryId) {
        Inventory inventory = inventoryRepository.findByIdForUpdate(inventoryId)
                .orElseThrow(() -> new RuntimeException("이벤트의 좌석 정보를 찾을 수 없습니다."));
        inventory.hold();
    }

    // Redisson 분산 락 적용
    @DistributedLock(key = "'INVENTORY_LOCK:' + #inventoryId")
    public void holdInventory(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("이벤트의 좌석 정보를 찾을 수 없습니다."));
        inventory.hold();
    }

    @Transactional
    public void confirmInventory(Long inventoryId) {
        Inventory inventory = inventoryRepository.findByIdForUpdate(inventoryId)
                .orElseThrow(() -> new RuntimeException("이벤트의 좌석 정보를 찾을 수 없습니다."));
        inventory.confirm();
    }

    @Transactional
    public void releaseInventory(Long inventoryId) {
        Inventory inventory = inventoryRepository.findByIdForUpdate(inventoryId)
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
