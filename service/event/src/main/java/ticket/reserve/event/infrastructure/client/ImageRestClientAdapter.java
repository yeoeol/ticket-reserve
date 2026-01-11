package ticket.reserve.event.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.event.application.dto.response.ImageResponseDto;
import ticket.reserve.event.application.port.out.ImagePort;

import java.io.IOException;

@Component
public class ImageRestClientAdapter implements ImagePort {

    private final RestClient restClient;

    public ImageRestClientAdapter(
            @Value("${endpoints.ticket-reserve-image-service.url}") String imageServiceUrl
    ) {
        this.restClient = RestClient.create(imageServiceUrl);
    }

    @Override
    public ImageResponseDto uploadImage(MultipartFile file, String userId) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            ByteArrayResource contentsAsResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename(); // 파일명이 있어야 서버에서 인식함
                }
            };
            body.add("file", contentsAsResource);
        } catch (IOException e) {
            throw new RuntimeException("파일 변환 실패", e);
        }

        return restClient.post()
                .uri("/api/images/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("X-USER-ID", userId)
                .header("X-User-Roles", "ROLE_ADMIN")
                .body(body)
                .retrieve()
                .body(ImageResponseDto.class);
    }
}
