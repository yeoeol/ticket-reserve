package ticket.reserve.busking.application.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import org.locationtech.jts.geom.Point;
import ticket.reserve.busking.domain.busking.Busking;
import ticket.reserve.busking.domain.buskingimage.BuskingImage;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record BuskingResponseDto(
        Long id,
        String title,          // 공연 제목
        String description,
        String location,            // 장소
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<String> imageUrls,
        Double latitude,
        Double longitude,
        boolean isSubscribed
) {
    public static BuskingResponseDto from(Busking busking, boolean isSubscribed) {
        Point point = busking.getCoordinate();

        return BuskingResponseDto.builder()
                .id(busking.getId())
                .title(busking.getTitle())
                .description(busking.getDescription())
                .location(busking.getLocation())
                .startTime(busking.getStartTime())
                .endTime(busking.getEndTime())
                .imageUrls(busking.getBuskingImages().stream()
                        .map(BuskingImage::getStoredPath)
                        .toList()
                )
                .latitude(point.getY())
                .longitude(point.getX())
                .isSubscribed(isSubscribed)
                .build();
    }

    public static BuskingResponseDto from(Busking busking) {
        return from(busking, false);
    }

    public BuskingResponseDto withSubscribed(boolean isSubscribed) {
        return BuskingResponseDto.builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .location(this.location)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .imageUrls(this.imageUrls)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .isSubscribed(isSubscribed)
                .build();
    }

    @QueryProjection
    public BuskingResponseDto {
    }
}