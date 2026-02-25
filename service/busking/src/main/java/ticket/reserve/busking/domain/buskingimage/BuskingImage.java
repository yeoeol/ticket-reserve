package ticket.reserve.busking.domain.buskingimage;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ticket.reserve.busking.domain.BaseTimeEntity;
import ticket.reserve.busking.domain.busking.Busking;
import ticket.reserve.busking.domain.buskingimage.enums.ImageType;
import ticket.reserve.core.tsid.IdGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "busking_images")
public class BuskingImage extends BaseTimeEntity {

    @Id
    @Column(name = "busking_image_id")
    private Long id;

    private Long imageId;

    private String originalFileName;
    private String storedPath;

    @Enumerated(EnumType.STRING)
    private ImageType type;
    private Integer sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "busking_id", nullable = false)
    private Busking busking;

    @Builder
    private BuskingImage(
            IdGenerator idGenerator, Long imageId, String originalFileName, String storedPath,
            ImageType type, Integer sortOrder, Busking busking
    ) {
        this.id = idGenerator.nextId();
        this.imageId = imageId;
        this.originalFileName = originalFileName;
        this.storedPath = storedPath;
        this.type = type;
        this.sortOrder = sortOrder;
        this.busking = busking;
    }

    public static BuskingImage create(
            IdGenerator idGenerator, Long imageId, String originalFileName, String storedPath,
            ImageType type, Integer sortOrder, Busking busking
    ) {
        return BuskingImage.builder()
                .idGenerator(idGenerator)
                .imageId(imageId)
                .originalFileName(originalFileName)
                .storedPath(storedPath)
                .type(type)
                .sortOrder(sortOrder)
                .busking(busking)
                .build();
    }
}
