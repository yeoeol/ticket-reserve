package ticket.reserve.event.application;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

}