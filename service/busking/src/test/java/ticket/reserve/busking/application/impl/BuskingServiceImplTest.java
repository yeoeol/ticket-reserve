package ticket.reserve.busking.application.impl;


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
import org.springframework.mock.web.MockMultipartFile;
import ticket.reserve.busking.application.BuskingPublishService;
import ticket.reserve.busking.application.BuskingQueryService;
import ticket.reserve.busking.application.BuskingService;
import ticket.reserve.busking.application.dto.request.BuskingUpdateRequestDto;
import ticket.reserve.busking.application.dto.response.ImageResponseDto;
import ticket.reserve.busking.application.port.out.ImagePort;
import ticket.reserve.busking.application.dto.request.BuskingRequestDto;
import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.application.port.out.NotificationSchedulePort;
import ticket.reserve.busking.application.port.out.SubscriptionPort;
import ticket.reserve.busking.domain.busking.Busking;
import ticket.reserve.busking.domain.busking.repository.BuskingRepository;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;
import ticket.reserve.core.tsid.IdGenerator;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BuskingServiceImplTest {
    @InjectMocks
    BuskingServiceImpl buskingService;

    @Mock
    private IdGenerator idGenerator;
    @Mock
    private BuskingPublishService buskingPublishService;
    @Mock
    private BuskingQueryService buskingQueryService;
    @Mock
    private BuskingRepository buskingRepository;
    @Mock
    private NotificationSchedulePort notificationSchedulePort;
    @Mock
    private ImagePort imagePort;
    @Mock
    private SubscriptionPort subscriptionPort;

    private Busking busking;

    @BeforeEach
    void setUp() {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point coordinate = geometryFactory.createPoint(new Coordinate(0, 0));

        busking = Busking.create(
                idGenerator,
                "testTitle",
                "testDesc",
                "장소",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                coordinate
        );
    }

    @Test
    @DisplayName("버스킹 생성 성공(파일 X) - 요청 정보를 기반으로 이벤트를 생성한다")
    void create_success() {
        //given
        BuskingRequestDto request = new BuskingRequestDto(
                "testTitle", "testDesc", "장소",
                busking.getStartTime(), busking.getEndTime(), 0.0, 0.0
        );

        ArgumentCaptor<Busking> buskingCaptor = ArgumentCaptor.forClass(Busking.class);
        given(buskingPublishService.publishBuskingCreatedEvent(buskingCaptor.capture()))
                .willReturn(busking);

        //when
        BuskingResponseDto response = buskingService.create(request, null);

        //then
        Busking savedBusking = buskingCaptor.getValue();

        assertThat(response.title()).isEqualTo(request.title());
        assertThat(response.description()).isEqualTo(request.description());
        assertThat(response.location()).isEqualTo(request.location());
        assertThat(response.startTime()).isEqualTo(request.startTime());
        assertThat(response.endTime()).isEqualTo(request.endTime());

        assertThat(savedBusking.getTitle()).isEqualTo(request.title());
        assertThat(savedBusking.getDescription()).isEqualTo(request.description());
        assertThat(savedBusking.getLocation()).isEqualTo(request.location());
        assertThat(savedBusking.getStartTime()).isEqualTo(request.startTime());
        assertThat(savedBusking.getEndTime()).isEqualTo(request.endTime());

        verify(imagePort, never()).uploadImage(any());
        verify(imagePort, never()).deleteImage(any());
    }

    @Test
    @DisplayName("버스킹 생성 성공(파일 O) - 요청 정보를 기반으로 이벤트를 생성한다")
    void create_success_with_file() {
        //given
        BuskingRequestDto request = new BuskingRequestDto(
                "testTitle", "testDesc", "장소",
                busking.getStartTime(), busking.getEndTime(), 0.0, 0.0
        );
        MockMultipartFile file = new MockMultipartFile(
                "test_file", "test.png", "image/png", new byte[10]);
        ImageResponseDto imageResponse = ImageResponseDto.builder()
                .imageId(123456L)
                .originalFileName(file.getOriginalFilename())
                .storedPath("testStoredPath")
                .userId(1234L)
                .build();

        ArgumentCaptor<Busking> buskingCaptor = ArgumentCaptor.forClass(Busking.class);
        given(buskingPublishService.publishBuskingCreatedEvent(buskingCaptor.capture()))
                .willReturn(busking);
        given(imagePort.uploadImage(file)).willReturn(imageResponse);

        //when
        BuskingResponseDto response = buskingService.create(request, file);

        //then
        Busking savedBusking = buskingCaptor.getValue();

        assertThat(response.title()).isEqualTo(request.title());
        assertThat(response.description()).isEqualTo(request.description());
        assertThat(response.location()).isEqualTo(request.location());
        assertThat(response.startTime()).isEqualTo(request.startTime());
        assertThat(response.endTime()).isEqualTo(request.endTime());

        assertThat(savedBusking.getTitle()).isEqualTo(request.title());
        assertThat(savedBusking.getDescription()).isEqualTo(request.description());
        assertThat(savedBusking.getLocation()).isEqualTo(request.location());
        assertThat(savedBusking.getStartTime()).isEqualTo(request.startTime());
        assertThat(savedBusking.getEndTime()).isEqualTo(request.endTime());

        assertThat(savedBusking.getBuskingImages()).hasSize(1);
        verify(imagePort, times(1)).uploadImage(any());
        verify(imagePort, never()).deleteImage(any());
    }

    @Test
    @DisplayName("버스킹 생성 실패 - 요청 정보를 기반으로 이벤트를 생성한다")
    void create_fail() {
        //given
        BuskingRequestDto request = new BuskingRequestDto(
                "testTitle", "testDesc", "장소",
                busking.getStartTime(), busking.getEndTime(), 0.0, 0.0
        );
        MockMultipartFile file = new MockMultipartFile(
                "test_file", "test.png", "image/png", new byte[10]);
        ImageResponseDto imageResponse = ImageResponseDto.builder()
                .imageId(123456L)
                .originalFileName(file.getOriginalFilename())
                .storedPath("testStoredPath")
                .userId(1234L)
                .build();

        given(imagePort.uploadImage(file)).willReturn(imageResponse);
        when(buskingPublishService.publishBuskingCreatedEvent(any(Busking.class)))
                .thenThrow(RuntimeException.class);

        //when & then
        assertThatThrownBy(() -> buskingService.create(request, file)).isInstanceOf(RuntimeException.class);

        verify(imagePort, times(1)).uploadImage(any());
        verify(imagePort, times(1)).deleteImage(any());
        assertThat(busking.getBuskingImages()).hasSize(0);
    }

    @Test
    @DisplayName("버스킹 수정 성공 - 수정 정보를 기반으로 버스킹 엔티티를 수정한다")
    void update_success() {
        //given
        BuskingUpdateRequestDto request = new BuskingUpdateRequestDto(
                "updateEventTitle", "updateDesc", "테스트장소",
                LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(20)
        );
        given(buskingQueryService.findById(busking.getId())).willReturn(busking);

        //when
        buskingService.update(busking.getId(), request);

        //then
        assertThat(busking.getTitle()).isEqualTo(request.title());
        assertThat(busking.getDescription()).isEqualTo(request.description());
        assertThat(busking.getLocation()).isEqualTo(request.location());
        assertThat(busking.getStartTime()).isEqualTo(request.startTime());
        assertThat(busking.getEndTime()).isEqualTo(request.endTime());
    }

    @Test
    @DisplayName("버스킹 수정 실패 - 입력된 'id'와 일치하는 버스킹이 존재하지 않을 때 예외가 발생한다")
    void updateEvent_fail_notFound() {
        //given
        BuskingUpdateRequestDto request = new BuskingUpdateRequestDto(
                "updateEventTitle", "updateDesc", "테스트장소",
                LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(20)
        );
        when(buskingQueryService.findById(9999L)).thenThrow(new CustomException(ErrorCode.BUSKING_NOT_FOUND));

        //when
        Throwable throwable = catchThrowable(() -> buskingService.update(9999L, request));

        //then
        assertThat(throwable)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.BUSKING_NOT_FOUND.getMessage())
                .extracting("errorCode")
                    .isEqualTo(ErrorCode.BUSKING_NOT_FOUND);
    }
}