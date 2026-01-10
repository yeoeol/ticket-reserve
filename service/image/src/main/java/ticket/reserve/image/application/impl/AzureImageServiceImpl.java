package ticket.reserve.image.application.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.image.application.ImageService;
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

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    @Transactional
    public String upload(MultipartFile file, String containerName) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        if (!containerClient.exists()) {
            containerClient.create();
        }

        validateExtension(file);
        validateFileSize(file);

        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        BlobClient blobClient = containerClient.getBlobClient(uniqueFileName);

        try {
            BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(file.getContentType());
            blobClient.upload(file.getInputStream(), file.getSize(), true);
            blobClient.setHttpHeaders(headers);
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }

        String storedPath = blobClient.getBlobUrl();
        imageRepository.save(Image.create(idGenerator, file.getOriginalFilename(), storedPath, null));

        return storedPath; // 업로드된 파일 URL 반환
    }

    private void validateExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new RuntimeException("허용되지 않는 확장자입니다.");
        }
    }

    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_SIZE) {
            throw new RuntimeException("파일 크기는 5MB를 초과할 수 없습니다.");
        }
    }
}
