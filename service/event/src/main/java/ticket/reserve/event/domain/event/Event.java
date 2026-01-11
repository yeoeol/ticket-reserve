package ticket.reserve.event.domain.event;

import jakarta.persistence.*;
import lombok.*;
import ticket.reserve.event.application.dto.request.EventUpdateRequestDto;
import ticket.reserve.event.domain.BaseTimeEntity;
import ticket.reserve.tsid.IdGenerator;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "events")
public class Event extends BaseTimeEntity {

    @Id
    @Column(name = "event_id")
    private Long id;

    private String eventTitle;          // 공연 제목
    private String description;         // 상세 내용
    private String location;            // 장소

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Integer totalInventoryCount;             // 총 좌석 수

    @Builder(access = AccessLevel.PRIVATE)
    private Event(IdGenerator idGenerator, String eventTitle, String description, String location, LocalDateTime startTime, LocalDateTime endTime, Integer totalInventoryCount) {
        this.id = idGenerator.nextId();
        this.eventTitle = eventTitle;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalInventoryCount = totalInventoryCount;
    }

    public static Event create(IdGenerator idGenerator, String eventTitle, String description, String location, LocalDateTime startTime, LocalDateTime endTime, Integer totalInventoryCount) {
        return Event.builder()
                .idGenerator(idGenerator)
                .eventTitle(eventTitle)
                .description(description)
                .location(location)
                .startTime(startTime)
                .endTime(endTime)
                .totalInventoryCount(totalInventoryCount)
                .build();
    }

    public void update(EventUpdateRequestDto request) {
        this.eventTitle = request.eventTitle();
        this.description = request.description();
        this.location = request.location();
        this.startTime = request.startTime();
        this.endTime = request.endTime();
        this.totalInventoryCount = request.totalInventoryCount();
    }
}
