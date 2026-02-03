package ticket.reserve.image.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ticket.reserve.core.tsid.IdGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "images")
public class Image extends BaseTimeEntity {

    @Id
    @Column(name = "image_id")
    private Long id;

    private String originalFileName;

    @Column(unique = true)
    private String uniqueFileName;

    private String storedPath;

    private Long userId;

    @Builder(access = AccessLevel.PRIVATE)
    private Image(IdGenerator idGenerator, String originalFileName, String uniqueFileName, String storedPath, Long userId) {
        this.id = idGenerator.nextId();
        this.originalFileName = originalFileName;
        this.uniqueFileName = uniqueFileName;
        this.storedPath = storedPath;
        this.userId = userId;
    }

    public static Image create(IdGenerator idGenerator, String originalFileName, String uniqueFileName, String storedPath, Long userId) {
        return Image.builder()
                .idGenerator(idGenerator)
                .originalFileName(originalFileName)
                .uniqueFileName(uniqueFileName)
                .storedPath(storedPath)
                .userId(userId)
                .build();
    }
}
