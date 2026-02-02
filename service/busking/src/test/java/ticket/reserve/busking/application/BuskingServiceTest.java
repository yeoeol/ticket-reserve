package ticket.reserve.busking.application;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.core.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.busking.application.dto.request.BuskingRequestDto;
import ticket.reserve.busking.application.dto.request.BuskingUpdateRequestDto;
import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.application.port.out.InventoryPort;
import ticket.reserve.busking.domain.busking.Busking;
import ticket.reserve.busking.domain.busking.repository.BuskingRepository;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;
import ticket.reserve.core.tsid.IdGenerator;

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

    @Mock InventoryPort inventoryPort;
    @Mock OutboxEventPublisher outboxEventPublisher;
    @Mock IdGenerator idGenerator;
    @Mock BuskingCrudService buskingCrudService;

    private Busking busking;

    @BeforeEach
    void setUp() {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point coordinate = geometryFactory.createPoint(new Coordinate(0, 0));

        busking = Busking.create(
                () -> 1234L,
                "testTitle",
                "testDesc",
                "장소",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                10,
                coordinate
        );
    }

    @Test
    @DisplayName("이벤트 생성 성공 - 요청 정보를 기반으로 이벤트를 생성한다")
    void createSuccess() {
        //given
        BuskingRequestDto request = new BuskingRequestDto(
                "testTitle", "testDesc", "장소",
                busking.getStartTime(), busking.getEndTime(), 0, 0.0, 0.0
        );

        ArgumentCaptor<Busking> eventCaptor = ArgumentCaptor.forClass(Busking.class);
        given(buskingCrudService.save(eventCaptor.capture()))
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
    }
}