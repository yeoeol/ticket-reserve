package ticket.reserve.event.dto;

import ticket.reserve.event.entity.Event;

import java.time.LocalDateTime;

public record EventRequestDto(
        String eventTitle,          // 공연 제목
        String description,         // 상세 내용
        String location,            // 장소
        LocalDateTime startTime,
        LocalDateTime endTime,
        int totalSeats,             // 총 좌석 수
        int availableSeats          // 남은 좌석 수
) {
    public Event toEntity() {
        return Event.builder()
                .eventTitle(this.eventTitle)
                .description(this.description)
                .location(this.location)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .totalSeats(this.totalSeats)
                .availableSeats(this.availableSeats)
                .build();
    }
}
