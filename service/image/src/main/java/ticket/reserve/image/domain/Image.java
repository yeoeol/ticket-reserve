package ticket.reserve.image.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ticket.reserve.tsid.IdGenerator;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "images")
public class Image {

    @Id
    @Column(name = "image_id")
    private Long id;

    private String originalFileName;
    private String storedPath;

    private Long userId;

    @Builder(access = AccessLevel.PRIVATE)
    private Image(IdGenerator idGenerator, String originalFileName, String storedPath, Long userId) {
        this.id = idGenerator.nextId();
        this.originalFileName = originalFileName;
        this.storedPath = storedPath;
        this.userId = userId;
    }

    public static Image create(IdGenerator idGenerator, String originalFileName, String storedPath, Long userId) {
        return Image.builder()
                .idGenerator(idGenerator)
                .originalFileName(originalFileName)
                .storedPath(storedPath)
                .userId(userId)
                .build();
    }
}
