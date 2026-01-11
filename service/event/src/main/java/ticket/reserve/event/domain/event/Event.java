package ticket.reserve.event.domain.event;

import jakarta.persistence.*;
import lombok.*;
import ticket.reserve.event.domain.BaseTimeEntity;
import ticket.reserve.event.domain.eventimage.EventImage;
import ticket.reserve.event.domain.eventimage.enums.ImageType;
import ticket.reserve.tsid.IdGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventImage> eventImages = new ArrayList<>();

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

    public void update(
            String eventTitle, String description, String location,
            LocalDateTime startTime, LocalDateTime endTime, Integer totalInventoryCount
    ) {
        this.eventTitle = eventTitle;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalInventoryCount = totalInventoryCount;
    }

    public void addEventImage(
            IdGenerator idGenerator, String originalFileName, String storedPath,
            ImageType type, Integer sortOrder
    ) {
        EventImage eventImage = EventImage.create(
                idGenerator, originalFileName, storedPath,
                type, 1, this
        );
        this.eventImages.add(eventImage);
    }
}
