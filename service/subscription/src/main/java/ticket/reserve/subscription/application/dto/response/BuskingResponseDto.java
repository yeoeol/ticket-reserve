package ticket.reserve.subscription.application.dto.response;

import lombok.Builder;

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
}