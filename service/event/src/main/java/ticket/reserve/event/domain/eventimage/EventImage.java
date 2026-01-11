package ticket.reserve.event.domain.eventimage;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ticket.reserve.event.domain.BaseTimeEntity;
import ticket.reserve.event.domain.eventimage.enums.ImageType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "event_images")
public class EventImage extends BaseTimeEntity {

    @Id
    @Column(name = "event_image_id")
    private Long id;
    private Long eventId;
    private String originalFileName;
    private String storedPath;

    @Enumerated(EnumType.STRING)
    private ImageType type;
    private Integer sortOrder;
}
