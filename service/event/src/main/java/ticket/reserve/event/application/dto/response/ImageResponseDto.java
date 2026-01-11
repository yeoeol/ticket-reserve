package ticket.reserve.event.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageResponseDto {
    private Long imageId;
    private String originalFileName;
    private String storedPath;
    private Long userId;
}
