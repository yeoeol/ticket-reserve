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
    @Column(name = "event_id")
    private Long id;

    private String title;          // 공연 제목
    private String description;         // 상세 내용
    private String location;            // 장소

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Integer totalInventoryCount;             // 총 좌석 수

    @Column(columnDefinition = "POINT SRID 4326")
    private Point coordinate;

    @OneToMany(mappedBy = "busking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BuskingImage> buskingImages = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Busking(IdGenerator idGenerator, String title, String description, String location, LocalDateTime startTime, LocalDateTime endTime, Integer totalInventoryCount, Point coordinate) {
        this.id = idGenerator.nextId();
        this.title = title;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalInventoryCount = totalInventoryCount;
        this.coordinate = coordinate;
    }

    public static Busking create(
            IdGenerator idGenerator, String title, String description,
            String location, LocalDateTime startTime, LocalDateTime endTime,
            Integer totalInventoryCount, Point coordinate
    ) {
        return Busking.builder()
                .idGenerator(idGenerator)
                .title(title)
                .description(description)
                .location(location)
                .startTime(startTime)
                .endTime(endTime)
                .totalInventoryCount(totalInventoryCount)
                .coordinate(coordinate)
                .build();
    }

    public void update(
            String title, String description, String location,
            LocalDateTime startTime, LocalDateTime endTime, Integer totalInventoryCount
    ) {
        this.title = title;
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
        BuskingImage buskingImage = BuskingImage.create(
                idGenerator, originalFileName, storedPath,
                type, sortOrder, this
        );
        this.buskingImages.add(buskingImage);
    }
}
