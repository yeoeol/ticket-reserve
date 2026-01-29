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
        Integer availableInventory,
        Integer totalInventoryCount,
        List<String> imageUrls,
        Double latitude,
        Double longitude
) {
    public static BuskingResponseDto from(Busking busking, Integer availableInventoryCount) {
        Point point = busking.getCoordinate();

        return BuskingResponseDto.builder()
                .id(busking.getId())
                .title(busking.getTitle())
                .description(busking.getDescription())
                .location(busking.getLocation())
                .startTime(busking.getStartTime())
                .endTime(busking.getEndTime())
                .availableInventory(availableInventoryCount)
                .totalInventoryCount(busking.getTotalInventoryCount())
                .imageUrls(busking.getBuskingImages().stream()
                        .map(BuskingImage::getStoredPath)
                        .toList()
                )
                .latitude(point.getY())
                .longitude(point.getX())
                .build();
    }

    @QueryProjection
    public BuskingResponseDto {
    }
}