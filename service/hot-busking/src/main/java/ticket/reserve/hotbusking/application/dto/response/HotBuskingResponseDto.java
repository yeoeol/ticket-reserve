package ticket.reserve.hotbusking.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record HotBuskingResponseDto(
        Long id,
        String title,          // 공연 제목
        String description,
        String location,            // 장소
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<String> imageUrls,
        Double latitude,
        Double longitude,
        Long subscriptionCount
) {
    public static HotBuskingResponseDto from(BuskingResponseDto buskingResponse, Long subscriptionCount) {
        return HotBuskingResponseDto.builder()
                .id(buskingResponse.id())
                .title(buskingResponse.title())
                .description(buskingResponse.description())
                .location(buskingResponse.location())
                .startTime(buskingResponse.startTime())
                .endTime(buskingResponse.endTime())
                .imageUrls(buskingResponse.imageUrls())
                .latitude(buskingResponse.latitude())
                .longitude(buskingResponse.longitude())
                .subscriptionCount(subscriptionCount)
                .build();
    }
}
