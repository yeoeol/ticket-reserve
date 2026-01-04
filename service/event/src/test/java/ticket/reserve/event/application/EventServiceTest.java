package ticket.reserve.event.application;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.common.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.event.application.dto.request.EventRequestDto;
import ticket.reserve.event.application.dto.request.EventUpdateRequestDto;
import ticket.reserve.event.application.dto.response.EventDetailResponseDto;
import ticket.reserve.event.application.port.out.InventoryPort;
import ticket.reserve.event.domain.Event;
import ticket.reserve.event.domain.repository.EventRepository;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @InjectMocks
    EventService eventService;

    @Mock EventRepository eventRepository;
    @Mock InventoryPort inventoryPort;
    @Mock OutboxEventPublisher outboxEventPublisher;

    private Event event;

    @BeforeEach
    void setUp() {
        event = Event.builder()
                .id(1234L)
                .eventTitle("testTitle")
                .description("testDesc")
                .location("장소")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .totalSeats(10)
                .build();
    }

    @Test
    @DisplayName("이벤트 생성 성공 - 요청 정보를 기반으로 이벤트를 생성한다")
    void createEventSuccess() {
        //given
        EventRequestDto request = new EventRequestDto(
                "testTitle", "testDesc", "장소",
                event.getStartTime(), event.getEndTime(), 10
        );

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        given(eventRepository.save(eventCaptor.capture()))
                .willReturn(event);

        //when
        EventDetailResponseDto response = eventService.createEvent(request);

        //then
        Event savedEvent = eventCaptor.getValue();

        assertThat(response.eventTitle()).isEqualTo(request.eventTitle());
        assertThat(response.description()).isEqualTo(request.description());
        assertThat(response.startTime()).isEqualTo(request.startTime());
        assertThat(response.endTime()).isEqualTo(request.endTime());

        assertThat(savedEvent.getEventTitle()).isEqualTo(request.eventTitle());
        assertThat(savedEvent.getDescription()).isEqualTo(request.description());
        assertThat(savedEvent.getStartTime()).isEqualTo(request.startTime());
        assertThat(savedEvent.getEndTime()).isEqualTo(request.endTime());
    }

    @Test
    @DisplayName("이벤트 수정 성공 - 수정 정보를 기반으로 이벤트 엔티티를 수정한다")
    void updateEventSuccess() {
        //given
        EventUpdateRequestDto request = new EventUpdateRequestDto(
                "updateEventTitle", "updateDesc", "테스트장소",
                LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(20), 20
        );
        given(eventRepository.findById(1234L))
                .willReturn(Optional.of(event));

        //when
        eventService.updateEvent(1234L, request);

        //then
        assertThat(event.getEventTitle()).isEqualTo(request.eventTitle());
        assertThat(event.getDescription()).isEqualTo(request.description());
        assertThat(event.getLocation()).isEqualTo(request.location());
        assertThat(event.getStartTime()).isEqualTo(request.startTime());
        assertThat(event.getEndTime()).isEqualTo(request.endTime());
        assertThat(event.getTotalSeats()).isEqualTo(request.totalSeats());
    }

    @Test
    @DisplayName("이벤트 수정 실패 - 입력된 'id'와 일치하는 이벤트가 존재하지 않을 때 예외가 발생한다")
    void updateEventFail_EventNotFound() {
        //given
        given(eventRepository.findById(9999L))
                .willReturn(Optional.empty());

        //when
        Throwable throwable = catchThrowable(() -> eventService.updateEvent(9999L, any()));

        //then
        assertThat(throwable)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.EVENT_NOT_FOUND.getMessage())
                .extracting("errorCode").isEqualTo(ErrorCode.EVENT_NOT_FOUND);
    }

}