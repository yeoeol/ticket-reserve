package ticket.reserve.busking.application.port.out;

import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.busking.application.dto.response.ImageResponseDto;

public interface ImagePort {
    ImageResponseDto uploadImage(MultipartFile file);

    void deleteImage(Long id);
}
