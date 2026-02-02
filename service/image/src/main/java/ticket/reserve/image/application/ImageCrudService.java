package ticket.reserve.image.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;
import ticket.reserve.image.domain.Image;
import ticket.reserve.image.domain.repository.ImageRepository;
import ticket.reserve.core.tsid.IdGenerator;

@Service
@RequiredArgsConstructor
public class ImageCrudService {

    private final ImageRepository imageRepository;
    private final IdGenerator idGenerator;

    @Transactional
    public Image save(String originalFileName, String storedPath, Long userId) {
        return imageRepository.save(
                Image.create(idGenerator, originalFileName, storedPath, userId)
        );
    }

    @Transactional(readOnly = true)
    public Image findById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));
    }

    @Transactional
    public void deleteById(Long id) {
        imageRepository.deleteById(id);
    }
}
