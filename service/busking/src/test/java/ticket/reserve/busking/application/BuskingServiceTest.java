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
import org.springframework.mock.web.MockMultipartFile;
import ticket.reserve.busking.application.dto.response.ImageResponseDto;
import ticket.reserve.busking.application.port.out.ImagePort;
import ticket.reserve.busking.application.dto.request.BuskingRequestDto;
import ticket.reserve.busking.application.dto.response.BuskingResponseDto;
import ticket.reserve.busking.application.port.out.InventoryPort;
import ticket.reserve.busking.domain.busking.Busking;
import ticket.reserve.core.tsid.IdGenerator;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BuskingServiceTest {
    @InjectMocks
    BuskingService buskingService;

    @Mock BuskingCrudService buskingCrudService;
    @Mock InventoryPort inventoryPort;
    @Mock ImagePort imagePort;
    @Mock IdGenerator idGenerator;

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
    @DisplayName("버스킹 생성 성공(파일 X) - 요청 정보를 기반으로 이벤트를 생성한다")
    void create_success() {
        //given
        BuskingRequestDto request = new BuskingRequestDto(
                "testTitle", "testDesc", "장소",
                busking.getStartTime(), busking.getEndTime(), 0, 0.0, 0.0
        );

        ArgumentCaptor<Busking> buskingCaptor = ArgumentCaptor.forClass(Busking.class);
        given(buskingCrudService.save(buskingCaptor.capture()))
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
                busking.getStartTime(), busking.getEndTime(), 0, 0.0, 0.0
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
        given(buskingCrudService.save(buskingCaptor.capture())).willReturn(busking);
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
                busking.getStartTime(), busking.getEndTime(), 0, 0.0, 0.0
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
        when(buskingCrudService.save(busking)).thenThrow(RuntimeException.class);

        //when & then
        assertThatThrownBy(() -> buskingService.create(request, file)).isInstanceOf(RuntimeException.class);

        verify(imagePort, times(1)).uploadImage(any());
        verify(imagePort, times(1)).deleteImage(any());
        assertThat(busking.getBuskingImages()).hasSize(0);
    }

}