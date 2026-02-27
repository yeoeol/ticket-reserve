package ticket.reserve.busking.domain.busking;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;
import ticket.reserve.busking.domain.BaseTimeEntity;
import ticket.reserve.busking.domain.buskingimage.BuskingImage;
import ticket.reserve.busking.domain.buskingimage.enums.ImageType;
import ticket.reserve.core.tsid.IdGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "buskings")
public class Busking extends BaseTimeEntity {

    @Id
    @Column(name = "busking_id")
    private Long id;

    private String title;          // 공연 제목
    private String description;         // 상세 내용
    private String location;            // 장소

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Column(columnDefinition = "POINT SRID 4326")
    private Point coordinate;

    @OneToMany(mappedBy = "busking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BuskingImage> buskingImages = new ArrayList<>();

    @Column(nullable = false)
    private Long userId;

    @Builder(access = AccessLevel.PRIVATE)
    private Busking(
            IdGenerator idGenerator, String title, String description,
            String location, LocalDateTime startTime, LocalDateTime endTime,
            Point coordinate, Long userId
    ) {
        this.id = idGenerator.nextId();
        this.title = title;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.coordinate = coordinate;
        this.userId = userId;
    }

    public static Busking create(
            IdGenerator idGenerator, String title, String description,
            String location, LocalDateTime startTime, LocalDateTime endTime,
            Point coordinate, Long userId
    ) {
        return Busking.builder()
                .idGenerator(idGenerator)
                .title(title)
                .description(description)
                .location(location)
                .startTime(startTime)
                .endTime(endTime)
                .coordinate(coordinate)
                .userId(userId)
                .build();
    }

    public void update(
            String title, String description, String location,
            LocalDateTime startTime, LocalDateTime endTime
    ) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void addEventImage(
            IdGenerator idGenerator, Long imageId, String originalFileName, String storedPath,
            ImageType type, Integer sortOrder
    ) {
        BuskingImage buskingImage = BuskingImage.create(
                idGenerator, imageId, originalFileName, storedPath,
                type, sortOrder, this
        );
        this.buskingImages.add(buskingImage);
    }
}
