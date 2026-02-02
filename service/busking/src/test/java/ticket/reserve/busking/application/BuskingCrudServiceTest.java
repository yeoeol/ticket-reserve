package ticket.reserve.busking.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.busking.application.dto.request.BuskingUpdateRequestDto;
import ticket.reserve.busking.domain.busking.Busking;
import ticket.reserve.busking.domain.busking.repository.BuskingRepository;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;
import ticket.reserve.core.outboxmessagerelay.OutboxEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BuskingCrudServiceTest {

    @InjectMocks
    BuskingCrudService buskingCrudService;

    @Mock
    private BuskingRepository buskingRepository;
    @Mock
    private OutboxEventPublisher outboxEventPublisher;

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
        buskingCrudService.update(1234L, request);

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
        Throwable throwable = catchThrowable(() -> buskingCrudService.update(9999L, request));

        //then
        assertThat(throwable)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.BUSKING_NOT_FOUND.getMessage())
                .extracting("errorCode").isEqualTo(ErrorCode.BUSKING_NOT_FOUND);
    }

}
