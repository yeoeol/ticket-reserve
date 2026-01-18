package ticket.reserve.busking.presentation.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import ticket.reserve.busking.application.BuskingService;
import ticket.reserve.busking.application.dto.request.BuskingRequestDto;
import ticket.reserve.busking.application.dto.response.EventResponseDto;
import ticket.reserve.busking.domain.event.Busking;
import ticket.reserve.global.exception.GlobalExceptionRestHandler;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@WebMvcTest(controllers = {BuskingApiController.class})
@Import(GlobalExceptionRestHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class BuskingApiControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    BuskingService buskingService;

    @Test
    @DisplayName("이벤트 조회 컨트롤러 성공 - GET /api/events 호출 시 이벤트 리스트를 조회한다")
    void getEventsSuccess() throws Exception {
        given(buskingService.getAll())
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

    @Test
    @DisplayName("이벤트 생성 실패 - POST /api/events 요청 정보 필드에 null이 들어있으면 예외가 발생한다")
    void handleValidationException() throws Exception {
        //given
        Busking event = createEvent(1L);
        BuskingRequestDto invalidRequest = new BuskingRequestDto(
                null, null, event.getLocation(),
                event.getStartTime(), event.getEndTime(), 0
        );

        //when & then
        assertThat(mvc.post().uri("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .apply(print())
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .hasPathSatisfying("$.code", v -> v.assertThat()
                        .isEqualTo("400 BAD_REQUEST"))
                .hasPathSatisfying("$.message", v -> v.assertThat()
                        .isEqualTo("입력 값 검증에 실패했습니다."))
                .hasPathSatisfying("$.errors.size()", v -> v.assertThat()
                        .isEqualTo(2))
                .hasPathSatisfying("$.errors[*].field", v -> v.assertThat()
                        .asInstanceOf(LIST).containsExactlyInAnyOrder("eventTitle", "description"));
    }

    private Busking createEvent(Long id) {
        String eventTitle = "testTitle"+id;
        String description = "testDescription"+id;
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 0, 0);
        return Busking.create(
                () -> id,
                eventTitle,
                description,
                "test",
                start,
                start.plusDays(1),
                10
        );
    }
}