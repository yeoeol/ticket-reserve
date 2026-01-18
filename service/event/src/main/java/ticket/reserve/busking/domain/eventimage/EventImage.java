package ticket.reserve.busking.domain.eventimage;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ticket.reserve.busking.domain.BaseTimeEntity;
import ticket.reserve.busking.domain.event.Event;
import ticket.reserve.busking.domain.eventimage.enums.ImageType;
import ticket.reserve.tsid.IdGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "event_images")
public class EventImage extends BaseTimeEntity {

    @Id
    @Column(name = "event_image_id")
    private Long id;
    private String originalFileName;
    private String storedPath;

    @Enumerated(EnumType.STRING)
    private ImageType type;
    private Integer sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Builder
    private EventImage(
            IdGenerator idGenerator, String originalFileName, String storedPath,
            ImageType type, Integer sortOrder, Event event
    ) {
        this.id = idGenerator.nextId();
        this.originalFileName = originalFileName;
        this.storedPath = storedPath;
        this.type = type;
        this.sortOrder = sortOrder;
        this.event = event;
    }

    public static EventImage create(
            IdGenerator idGenerator, String originalFileName, String storedPath,
            ImageType type, Integer sortOrder, Event event
    ) {
        return EventImage.builder()
                .idGenerator(idGenerator)
                .originalFileName(originalFileName)
                .storedPath(storedPath)
                .type(type)
                .sortOrder(sortOrder)
                .event(event)
                .build();
    }
}
