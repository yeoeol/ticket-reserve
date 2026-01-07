package ticket.reserve.event.presentation.api;

import jakarta.validation.Valid;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import ticket.reserve.event.application.EventService;
import ticket.reserve.event.application.dto.response.EventResponseDto;
import ticket.reserve.event.domain.Event;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(EventApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class EventApiControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    EventService eventService;

    @Test
    @DisplayName("이벤트 조회 컨트롤러 성공 - GET /api/events 호출 시 이벤트 리스트를 조회한다")
    void getEventsSuccess() throws Exception {
        given(eventService.getAllEvents())
                .willReturn(List.of(
                        EventResponseDto.from(createEvent(1L)),
                        EventResponseDto.from(createEvent(2L))
                ));

        assertThat(mvc.get().uri("/api/events"))
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.size()", v -> v.assertThat().isEqualTo(2))
                .hasPathSatisfying("$[0].id", v -> v.assertThat().isEqualTo(1))
                .hasPathSatisfying("$[0].eventTitle", v -> v.assertThat().isEqualTo("testTitle1"))
                .hasPathSatisfying("$[1].id", v -> v.assertThat().isEqualTo(2))
                .hasPathSatisfying("$[1].eventTitle", v -> v.assertThat().isEqualTo("testTitle2"));
    }

    private Event createEvent(Long id) {
        String eventTitle = "testTitle"+id;
        String description = "testDescription"+id;
        return Event.builder()
                .id(id)
                .eventTitle(eventTitle)
                .description(description)
                .location("test")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1))
                .totalSeats(10)
                .build();
    }
}