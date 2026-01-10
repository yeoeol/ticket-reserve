package ticket.reserve.image.application;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String upload(MultipartFile file, String containerName);
}
