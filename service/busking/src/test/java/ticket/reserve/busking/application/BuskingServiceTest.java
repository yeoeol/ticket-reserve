package ticket.reserve.busking.application;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.common.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.busking.application.dto.request.BuskingRequestDto;
import ticket.reserve.busking.application.dto.request.BuskingUpdateRequestDto;
import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.application.port.out.InventoryPort;
import ticket.reserve.busking.domain.event.Busking;
import ticket.reserve.busking.domain.event.repository.BuskingRepository;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.tsid.IdGenerator;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class BuskingServiceTest {
    @InjectMocks
    BuskingService buskingService;

    @Mock
    BuskingRepository buskingRepository;
    @Mock InventoryPort inventoryPort;
    @Mock OutboxEventPublisher outboxEventPublisher;
    @Mock IdGenerator idGenerator;

    private Busking busking;

    @BeforeEach
    void setUp() {
        busking = Busking.create(
                () -> 1234L,
                "testTitle",
                "testDesc",
                "장소",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                10
        );
    }

    @Test
    @DisplayName("이벤트 생성 성공 - 요청 정보를 기반으로 이벤트를 생성한다")
    void createSuccess() {
        //given
        BuskingRequestDto request = new BuskingRequestDto(
                "testTitle", "testDesc", "장소",
                busking.getStartTime(), busking.getEndTime(), 0
        );

        ArgumentCaptor<Busking> eventCaptor = ArgumentCaptor.forClass(Busking.class);
        given(buskingRepository.save(eventCaptor.capture()))
                .willReturn(busking);

        //when
        BuskingResponseDto response = buskingService.create(request, null);

        //then
        Busking savedEvent = eventCaptor.getValue();

        assertThat(response.title()).isEqualTo(request.title());
        assertThat(response.description()).isEqualTo(request.description());
        assertThat(response.location()).isEqualTo(request.location());
        assertThat(response.startTime()).isEqualTo(request.startTime());
        assertThat(response.endTime()).isEqualTo(request.endTime());

        assertThat(savedEvent.getTitle()).isEqualTo(request.title());
        assertThat(savedEvent.getDescription()).isEqualTo(request.description());
        assertThat(savedEvent.getLocation()).isEqualTo(request.location());
        assertThat(savedEvent.getStartTime()).isEqualTo(request.startTime());
        assertThat(savedEvent.getEndTime()).isEqualTo(request.endTime());

        verify(outboxEventPublisher, times(1)).publish(any(), any(), any());
    }

    @Test
    @DisplayName("이벤트 수정 성공 - 수정 정보를 기반으로 이벤트 엔티티를 수정한다")
    void updateSuccess() {
        //given
        BuskingUpdateRequestDto request = new BuskingUpdateRequestDto(
                "updateEventTitle", "updateDesc", "테스트장소",
                LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(20), 20
        );
        given(buskingRepository.findById(1234L))
                .willReturn(Optional.of(busking));

        //when
        buskingService.update(1234L, request);

        //then
        assertThat(busking.getTitle()).isEqualTo(request.title());
        assertThat(busking.getDescription()).isEqualTo(request.description());
        assertThat(busking.getLocation()).isEqualTo(request.location());
        assertThat(busking.getStartTime()).isEqualTo(request.startTime());
        assertThat(busking.getEndTime()).isEqualTo(request.endTime());
        assertThat(busking.getTotalInventoryCount()).isEqualTo(request.totalInventoryCount());
    }

    @Test
    @DisplayName("이벤트 수정 실패 - 입력된 'id'와 일치하는 이벤트가 존재하지 않을 때 예외가 발생한다")
    void updateEventFail_NotFound() {
        //given
        BuskingUpdateRequestDto request = new BuskingUpdateRequestDto(
                "updateEventTitle", "updateDesc", "테스트장소",
                LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(20), 20
        );
        given(buskingRepository.findById(9999L))
                .willReturn(Optional.empty());

        //when
        Throwable throwable = catchThrowable(() -> buskingService.update(9999L, request));

        //then
        assertThat(throwable)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.BUSKING_NOT_FOUND.getMessage())
                .extracting("errorCode").isEqualTo(ErrorCode.BUSKING_NOT_FOUND);
    }

}