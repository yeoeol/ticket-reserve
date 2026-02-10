package ticket.reserve.image.application;

import ticket.reserve.image.domain.Image;

public interface ImageCrudService {
    /**
     * 파일 이름과 저장소URL, 사용자 식별 값을 받아서 이미지를 저장한다.
     */
    Image save(String originalFileName, String uniqueFileName, String storedPath, Long userId);

    /**
     * 이미지를 조회한다.
     */
    Image findById(Long id);

    /**
     * 이미지를 삭제한다.
     */
    void deleteById(Long id);
}
