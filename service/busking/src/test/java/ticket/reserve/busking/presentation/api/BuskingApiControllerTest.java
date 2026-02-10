package ticket.reserve.busking.presentation.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import ticket.reserve.busking.application.BuskingQueryService;
import ticket.reserve.busking.application.BuskingService;
import ticket.reserve.busking.application.SearchService;
import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.domain.busking.Busking;
import ticket.reserve.core.global.exception.GlobalExceptionRestHandler;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@WebMvcTest(controllers = {BuskingApiController.class})
@Import(GlobalExceptionRestHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class BuskingApiControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private BuskingService buskingService;
    @MockitoBean
    private BuskingQueryService buskingQueryService;
    @MockitoBean
    private SearchService searchService;

    @Test
    @DisplayName("이벤트 조회 컨트롤러 성공 - GET /api/events 호출 시 이벤트 리스트를 조회한다")
    void getEventsSuccess() throws Exception {
        given(searchService.search(null, null, null, null))
                .willReturn(List.of(
                        BuskingResponseDto.from(createBusking(123L), 0),
                        BuskingResponseDto.from(createBusking(234L), 0)
                ));

        assertThat(mvc.get().uri("/api/buskings"))
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.size()", v -> v.assertThat().isEqualTo(2))
                .hasPathSatisfying("$[0].id", v -> v.assertThat().isEqualTo(123))
                .hasPathSatisfying("$[0].title", v -> v.assertThat().isEqualTo("testTitle123"))
                .hasPathSatisfying("$[1].id", v -> v.assertThat().isEqualTo(234))
                .hasPathSatisfying("$[1].title", v -> v.assertThat().isEqualTo("testTitle234"));
    }

    private Busking createBusking(Long id) {
        String eventTitle = "testTitle"+id;
        String description = "testDescription"+id;
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 0, 0);

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point coordinate = geometryFactory.createPoint(new Coordinate(0, 0));

        return Busking.create(
                () -> id,
                eventTitle,
                description,
                "test",
                start,
                start.plusDays(1),
                10,
                coordinate
        );
    }
}