package ticket.reserve.busking.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.busking.application.dto.response.ImageResponseDto;
import ticket.reserve.busking.application.port.out.ImagePort;
import ticket.reserve.core.global.exception.CustomException;
import ticket.reserve.core.global.exception.ErrorCode;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ImageRestClientAdapter implements ImagePort {

    private final RestClient imageRestClient;

    @Override
    public ImageResponseDto uploadImage(MultipartFile file) {
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
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAIL);
        }

        return imageRestClient.post()
                .uri("/api/images/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(ImageResponseDto.class);
    }

    @Override
    public void deleteImage(Long id) {
        imageRestClient.delete()
                .uri("/api/images/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
