package ticket.reserve.inventory.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.common.event.Event;
import ticket.reserve.common.event.EventPayload;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.inventory.application.dto.response.CustomPageResponse;
import ticket.reserve.inventory.application.eventhandler.EventHandler;
import ticket.reserve.inventory.global.annotation.DistributedLock;
import ticket.reserve.inventory.application.port.out.EventPort;
import ticket.reserve.inventory.application.dto.response.InventoryListResponseDto;
import ticket.reserve.inventory.application.dto.request.InventoryRequestDto;
import ticket.reserve.inventory.application.dto.response.InventoryResponseDto;
import ticket.reserve.inventory.application.dto.request.InventoryUpdateRequestDto;
import ticket.reserve.inventory.application.dto.response.EventDetailResponseDto;
import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.inventory.domain.repository.InventoryRepository;
import ticket.reserve.tsid.IdGenerator;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final List<EventHandler> eventHandlers;
    private final EventPort eventPort;
    private final IdGenerator idGenerator;

    @Transactional
    public void createInventory(InventoryRequestDto request) {
        EventDetailResponseDto eventResponseDto = eventPort.getOne(request.eventId());
        Integer eventInventoryCount = inventoryRepository.countInventoryByEventId(request.eventId());
        if (eventResponseDto.totalInventoryCount() == eventInventoryCount) {
            throw new CustomException(ErrorCode.INVENTORY_EXCEED);
        }

        Inventory inventory = request.toEntity(idGenerator);
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void updateInventory(Long inventoryId, InventoryUpdateRequestDto request) {
        Inventory inventory = getInventoryById(inventoryId);
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
        Inventory inventory = getInventoryById(inventoryId);

        return InventoryResponseDto.from(inventory);
    }

    // 락 미적용
    @Transactional
    public void holdInventoryV1(Long inventoryId) {
        Inventory inventory = getInventoryById(inventoryId);
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
        Inventory inventory = getInventoryById(inventoryId);
        inventory.hold();
    }

    @Transactional(readOnly = true)
    public Integer getAvailableInventoryCounts(Long eventId) {
        return inventoryRepository.countAvailableInventoryByEventId(eventId);
    }

    @Transactional
    public void deleteInventory(Long inventoryId) {
        inventoryRepository.deleteById(inventoryId);
    }

    public void handleEvent(Event<EventPayload> event) {
        EventHandler<EventPayload> eventHandler = findEventHandler(event);
        if (eventHandler == null) {
            return;
        }

        eventHandler.handle(event);
    }

    private Inventory getInventoryById(Long inventoryId) {
        return inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
    }

    private EventHandler<EventPayload> findEventHandler(Event<EventPayload> event) {
        return eventHandlers.stream()
                .filter(eventHandler -> eventHandler.supports(event))
                .findAny()
                .orElse(null);
    }
}
