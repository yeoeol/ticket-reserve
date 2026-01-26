package ticket.reserve.inventory.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventPayload;
import ticket.reserve.core.event.EventType;
import ticket.reserve.core.event.payload.BuskingCreatedEventPayload;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;
import ticket.reserve.core.inbox.Inbox;
import ticket.reserve.core.inbox.InboxRepository;
import ticket.reserve.inventory.application.dto.request.InventoryRequestDto;
import ticket.reserve.inventory.application.dto.request.InventoryUpdateRequestDto;
import ticket.reserve.inventory.application.dto.response.BuskingResponseDto;
import ticket.reserve.inventory.application.eventhandler.BuskingCreatedEventHandler;
import ticket.reserve.inventory.application.eventhandler.EventHandler;
import ticket.reserve.inventory.application.port.out.BuskingPort;
import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.inventory.domain.enums.InventoryStatus;
import ticket.reserve.inventory.domain.repository.InventoryRepository;
import ticket.reserve.core.tsid.IdGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceUnitTest {

    @InjectMocks
    InventoryService inventoryService;

    @Mock InventoryRepository inventoryRepository;
    @Mock InboxRepository inboxRepository;
    @Mock List<EventHandler> eventHandlers;
    @Mock BuskingPort buskingPort;
    @Mock IdGenerator idGenerator;

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = Inventory.create(
                () -> 1L, 1L, "TEST_001", 1000
        );
        BuskingCreatedEventHandler handler = new BuskingCreatedEventHandler(inventoryRepository, idGenerator);
        ReflectionTestUtils.setField(inventoryService, "eventHandlers", List.of(handler));
    }

    @Test
    @DisplayName("좌석 생성 성공 - 좌석 요청 정보를 기반으로 좌석을 생성한다")
    void createInventorySuccess() {
        //given
        InventoryRequestDto inventoryRequestDto = new InventoryRequestDto(
                "TEST_001", 1L, 1000
        );
        BuskingResponseDto buskingResponseDto = new BuskingResponseDto(
                1L, "testTitle", "testDesc", "테스트장소",
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), 10, 10, List.of()
        );
        given(buskingPort.getOne(1L)).willReturn(buskingResponseDto);
        given(inventoryRepository.countInventoryByBuskingId(1L)).willReturn(0);

        //when
        inventoryService.createInventory(inventoryRequestDto);

        //then
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    @DisplayName("좌석 생성 실패 - 좌석 개수 초과로 인해 좌석 생성 실패")
    void createInventoryFail_InventoryExceed() {
        //given
        InventoryRequestDto inventoryRequestDto = new InventoryRequestDto(
                "TEST_001", 1L, 1000
        );
        BuskingResponseDto buskingResponseDto = new BuskingResponseDto(
                1L, "testTitle", "testDesc", "테스트장소",
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), 10, 10, List.of()
        );
        given(buskingPort.getOne(1L)).willReturn(buskingResponseDto);
        given(inventoryRepository.countInventoryByBuskingId(1L)).willReturn(10);

        //when
        Throwable throwable = catchThrowable(() -> inventoryService.createInventory(inventoryRequestDto));

        //then
        assertThat(throwable)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVENTORY_EXCEED.getMessage())
                .extracting("errorCode").isEqualTo(ErrorCode.INVENTORY_EXCEED);
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    @DisplayName("좌석 수정 성공 - 수정 요청 정보를 기반으로 엔티티를 수정한다")
    void updateInventorySuccess() {
        //given
        InventoryUpdateRequestDto request = new InventoryUpdateRequestDto(
                "TEST_FAIL", 1000000
        );
        given(inventoryRepository.findByIdAndBuskingId(1L, 1L)).willReturn(Optional.of(inventory));

        //when
        inventoryService.updateInventory(1L, 1L, request);

        //then
        assertThat(inventory.getInventoryName()).isEqualTo(request.inventoryName());
        assertThat(inventory.getPrice()).isEqualTo(request.price());
    }

    @Test
    @DisplayName("좌석 선점 V1 성공(락 적용X) - 좌석 선점 메서드를 호출하여 좌석 상태가 PENDING 으로 변경된다")
    void holdInventoryV1Success() {
        //given
        given(inventoryRepository.findByIdAndBuskingId(1L, 1L)).willReturn(Optional.of(inventory));

        //when
        inventoryService.holdInventoryV1(1L,1L);

        //then
        assertThat(inventory.getStatus()).isEqualTo(InventoryStatus.PENDING);
    }

    @Test
    @DisplayName("좌석 선점 V1 실패(락 적용X) - 이미 선점된 좌석에 대해 선점 시도 시 예외가 발생한다")
    void holdInventoryV1Fail_InventoryHoldFail() {
        //given
        inventory.hold();   // 좌석을 선점 상태로 변경해주기 위해 먼저 호출
        given(inventoryRepository.findByIdAndBuskingId(1L, 1L)).willReturn(Optional.of(inventory));

        //when
        Throwable throwable = catchThrowable(() -> inventoryService.holdInventoryV1(1L, 1L));

        //then
        assertThat(throwable)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVENTORY_HOLD_FAIL.getMessage())
                .extracting("errorCode").isEqualTo(ErrorCode.INVENTORY_HOLD_FAIL);
    }

    @Test
    @DisplayName("좌석 선점 V2 성공(비관적 락) - 좌석 선점 메서드를 호출하여 좌석 상태가 PENDING 으로 변경된다")
    void holdInventoryV2Success() {
        //given
        given(inventoryRepository.findByIdForUpdate(1234L)).willReturn(Optional.of(inventory));

        //when
        inventoryService.holdInventoryV2(1234L);

        //then
        assertThat(inventory.getStatus()).isEqualTo(InventoryStatus.PENDING);
    }

    @Test
    @DisplayName("좌석 선점 성공(분산 락) - 좌석 선점 메서드를 호출하여 좌석 상태가 PENDING 으로 변경된다")
    void holdInventoryDistributedLockSuccess() {
        //given
        given(inventoryRepository.findByIdAndBuskingId(1L, 1L)).willReturn(Optional.of(inventory));

        //when
        inventoryService.holdInventory(1L, 1L);

        //then
        assertThat(inventory.getStatus()).isEqualTo(InventoryStatus.PENDING);
    }

    @Test
    @DisplayName("이벤트 수신 시 정확히 한 번 소비 로직이 수행된다")
    void handle_Event_OnceProcess() {
        //given
        Event<EventPayload> event = createEvent();
        given(inboxRepository.existsByEventId(event.getEventId())).willReturn(false);

        //when
        inventoryService.handleEvent(event);

        //then
        verify(inboxRepository, times(1)).saveAndFlush(any(Inbox.class));
    }

    @Test
    @DisplayName("이벤트 중복 수신 시 save가 호출되지 않는다")
    void handle_Event_DuplicateProcess() {
        //given
        Event<EventPayload> event = createEvent();
        given(inboxRepository.existsByEventId(event.getEventId())).willReturn(true);

        //when
        inventoryService.handleEvent(event);

        //then
        verify(inboxRepository, never()).saveAndFlush(any());
    }

    private static Event<EventPayload> createEvent() {
        return Event.of(1234L, EventType.BUSKING_CREATED,
                BuskingCreatedEventPayload.builder()
                        .buskingId(1L)
                        .title("testTitle")
                        .description("testDesc")
                        .location("testLoc")
                        .startTime(LocalDateTime.now())
                        .endTime(LocalDateTime.now().plusDays(1))
                        .totalInventoryCount(10)
                        .build()
        );
    }
}
