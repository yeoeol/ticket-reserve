package ticket.reserve.image.application.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;
import ticket.reserve.image.application.ImageCrudService;
import ticket.reserve.image.application.ImageService;
import ticket.reserve.image.application.dto.response.ImageResponseDto;
import ticket.reserve.image.domain.Image;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureImageServiceImpl implements ImageService {

    private final BlobServiceClient blobServiceClient;
    private final ImageCrudService imageCrudService;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    private final String CONTAINER_NAME = "busking";

    @Value("${spring.servlet.multipart.max-file-size}")
    private DataSize maxSize;

    @PostConstruct
    public void init() {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);
        if (!containerClient.exists()) {
            throw new CustomException(ErrorCode.NOT_FOUND_BLOB_CONTAINER);
        }
    }

    @Override
    public ImageResponseDto upload(MultipartFile file, Long userId) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);

        validateExtension(file);
        validateFileSize(file);

        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        BlobClient blobClient = containerClient.getBlobClient(uniqueFileName);

        // Azure 업로드
        try {
            BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(file.getContentType());
            blobClient.upload(file.getInputStream(), file.getSize(), true);
            blobClient.setHttpHeaders(headers);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAIL);
        }

        // DB 저장
        try {
            String storedPath = blobClient.getBlobUrl();
            Image savedImage = imageCrudService.save(file.getOriginalFilename(), storedPath, userId);
            return ImageResponseDto.from(savedImage);
        } catch (Exception e) {
            log.error("Azure 이미지 업로드 실패 - 보상 트랜잭션 실행 : {}", uniqueFileName, e);
            deleteFromAzure(blobClient);
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAIL);
        }
    }

    private void deleteFromAzure(BlobClient blobClient) {
        try {
            blobClient.deleteIfExists();
            log.info("보상 트랜잭션 성공: Azure에서 파일 삭제 완료 - {}", blobClient.getBlobName());
        } catch (Exception e) {
            log.error("보상 트랜잭션 실패: Azure 파일 삭제 중 오류 발생 - {}", blobClient.getBlobUrl(), e);
        }
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
        if (file.getSize() > maxSize.toBytes()) {
            throw new CustomException(ErrorCode.FILE_SIZE_EXCEED);
        }
    }
}
