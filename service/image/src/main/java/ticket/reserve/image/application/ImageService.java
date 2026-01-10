package ticket.reserve.image.application;

import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.image.application.dto.response.ImageResponseDto;

public interface ImageService {
    ImageResponseDto upload(MultipartFile file);
}
