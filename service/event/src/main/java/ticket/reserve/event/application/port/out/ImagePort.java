package ticket.reserve.event.application.port.out;

import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.event.application.dto.response.ImageResponseDto;

public interface ImagePort {
    ImageResponseDto uploadImage(MultipartFile file);
}
