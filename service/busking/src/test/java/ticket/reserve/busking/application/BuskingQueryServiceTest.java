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
public class BuskingQueryServiceTest {

    @InjectMocks
    BuskingQueryService buskingQueryService;

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
                coordinate
        );
    }
}
