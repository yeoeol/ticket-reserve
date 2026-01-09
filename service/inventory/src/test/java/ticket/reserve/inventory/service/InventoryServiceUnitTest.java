package ticket.reserve.inventory.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.inventory.application.InventoryService;
import ticket.reserve.inventory.application.dto.request.InventoryRequestDto;
import ticket.reserve.inventory.application.dto.request.InventoryUpdateRequestDto;
import ticket.reserve.inventory.application.dto.response.EventDetailResponseDto;
import ticket.reserve.inventory.application.eventhandler.EventHandler;
import ticket.reserve.inventory.application.port.out.EventPort;
import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.inventory.domain.enums.InventoryStatus;
import ticket.reserve.inventory.domain.repository.InventoryRepository;

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
    @Mock List<EventHandler> eventHandlers;
    @Mock EventPort eventPort;

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = Inventory.create(
                () -> 1L, 1L, "TEST_001", 1000
        );
    }

    @Test
    @DisplayName("좌석 생성 성공 - 좌석 요청 정보를 기반으로 좌석을 생성한다")
    void createInventorySuccess() {
        //given
        InventoryRequestDto inventoryRequestDto = new InventoryRequestDto(
                "TEST_001", 1L, 1000
        );
        EventDetailResponseDto eventDetailResponseDto = new EventDetailResponseDto(
                1L, "testTitle", "testDesc", "테스트장소",
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), 10, 10
        );
        given(eventPort.getOne(1L)).willReturn(eventDetailResponseDto);
        given(inventoryRepository.countInventoryByEventId(1L)).willReturn(0);

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
        EventDetailResponseDto eventDetailResponseDto = new EventDetailResponseDto(
                1L, "testTitle", "testDesc", "테스트장소",
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), 10, 10
        );
        given(eventPort.getOne(1L)).willReturn(eventDetailResponseDto);
        given(inventoryRepository.countInventoryByEventId(1L)).willReturn(10);

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
        given(inventoryRepository.findById(1234L)).willReturn(Optional.of(inventory));

        //when
        inventoryService.updateInventory(1234L, request);

        //then
        assertThat(inventory.getInventoryName()).isEqualTo(request.inventoryName());
        assertThat(inventory.getPrice()).isEqualTo(request.price());
    }

    @Test
    @DisplayName("좌석 선점 V1 성공(락 적용X) - 좌석 선점 메서드를 호출하여 좌석 상태가 PENDING 으로 변경된다")
    void holdInventoryV1Success() {
        //given
        given(inventoryRepository.findById(1234L)).willReturn(Optional.of(inventory));

        //when
        inventoryService.holdInventoryV1(1234L);

        //then
        assertThat(inventory.getStatus()).isEqualTo(InventoryStatus.PENDING);
    }

    @Test
    @DisplayName("좌석 선점 V1 실패(락 적용X) - 이미 선점된 좌석에 대해 선점 시도 시 예외가 발생한다")
    void holdInventoryV1Fail_InventoryHoldFail() {
        //given
        inventory.hold();   // 좌석을 선점 상태로 변경해주기 위해 먼저 호출
        given(inventoryRepository.findById(1234L)).willReturn(Optional.of(inventory));

        //when
        Throwable throwable = catchThrowable(() -> inventoryService.holdInventoryV1(1234L));

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
        given(inventoryRepository.findById(1234L)).willReturn(Optional.of(inventory));

        //when
        inventoryService.holdInventory(1234L);

        //then
        assertThat(inventory.getStatus()).isEqualTo(InventoryStatus.PENDING);
    }
}
