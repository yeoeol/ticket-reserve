package ticket.reserve.image.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import ticket.reserve.image.domain.Image;

@Getter
@Builder
public class ImageResponseDto {
    private Long imageId;
    private String originalFileName;
    private String storedPath;
    private Long userId;

    public static ImageResponseDto from(Image image) {
        return ImageResponseDto.builder()
                .imageId(image.getId())
                .originalFileName(image.getOriginalFileName())
                .storedPath(image.getStoredPath())
                .userId(image.getUserId())
                .build();
    }
}
