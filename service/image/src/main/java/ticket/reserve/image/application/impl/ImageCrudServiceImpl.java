package ticket.reserve.image.application.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.image.application.ImageCrudService;
import ticket.reserve.image.domain.Image;
import ticket.reserve.image.domain.repository.ImageRepository;

@Service
@RequiredArgsConstructor
public class ImageCrudServiceImpl implements ImageCrudService {

    private final ImageRepository imageRepository;
    private final IdGenerator idGenerator;

    @Transactional
    public Image save(String originalFileName, String uniqueFileName, String storedPath, Long userId) {
        return imageRepository.save(
                Image.create(idGenerator, originalFileName, uniqueFileName, storedPath, userId)
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
