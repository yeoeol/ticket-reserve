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
import ticket.reserve.inventory.application.dto.response.EventDetailResponseDto;
import ticket.reserve.inventory.application.eventhandler.EventHandler;
import ticket.reserve.inventory.application.port.out.EventPort;
import ticket.reserve.inventory.domain.Inventory;
import ticket.reserve.inventory.domain.enums.InventoryStatus;
import ticket.reserve.inventory.domain.repository.InventoryRepository;

import java.time.LocalDateTime;
import java.util.List;

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
        inventory = Inventory.builder()
                .id(1234L)
                .eventId(1L)
                .inventoryName("TEST_001")
                .price(1000)
                .status(InventoryStatus.AVAILABLE)
                .build();
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
}
