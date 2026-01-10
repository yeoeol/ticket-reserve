package ticket.reserve.image.application.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.image.application.ImageService;
import ticket.reserve.image.application.dto.response.ImageResponseDto;
import ticket.reserve.image.domain.Image;
import ticket.reserve.image.domain.repository.ImageRepository;
import ticket.reserve.tsid.IdGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AzureImageServiceImpl implements ImageService {

    private final BlobServiceClient blobServiceClient;
    private final ImageRepository imageRepository;
    private final IdGenerator idGenerator;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    private final String CONTAINER_NAME = "busking";

    @PostConstruct
    public void init() {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);
        if (!containerClient.exists()) {
            throw new CustomException(ErrorCode.NOT_FOUND_BLOB_CONTAINER);
        }
    }

    @Override
    @Transactional
    public ImageResponseDto upload(MultipartFile file, Long userId) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);

        validateExtension(file);
        validateFileSize(file);

        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        BlobClient blobClient = containerClient.getBlobClient(uniqueFileName);

        try {
            BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(file.getContentType());
            blobClient.upload(file.getInputStream(), file.getSize(), true);
            blobClient.setHttpHeaders(headers);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAIL);
        }

        String storedPath = blobClient.getBlobUrl();
        Image savedImage = imageRepository.save(
                Image.create(idGenerator, file.getOriginalFilename(), storedPath, userId)
        );

        return ImageResponseDto.from(savedImage); // 업로드된 파일 URL 반환
    }

    private void validateExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_FILE_NAME);
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new CustomException(ErrorCode.NOT_ALLOWED_EXT);
        }
    }

    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_SIZE) {
            throw new CustomException(ErrorCode.FILE_SIZE_EXCEED);
        }
    }
}
