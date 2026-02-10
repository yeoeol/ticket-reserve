package ticket.reserve.image.application;

import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.image.application.dto.response.ImageResponseDto;

public interface ImageService {
    /**
     * MultipartFile과 사용자 식별 값을 받아서 이미지를 업로드한다.
     */
    ImageResponseDto upload(MultipartFile file, Long userId);

    /**
     * 이미지 식별 값과 사용자 식별 값을 받아서 이미지를 삭제한다.
     */
    void delete(Long id, Long userId);
}
