package ticket.reserve.image.presentation.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ticket.reserve.image.application.ImageService;
import ticket.reserve.image.application.dto.response.ImageResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageApiController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<ImageResponseDto> uploadImage(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal String userId
    ) {
        return ResponseEntity.ok(imageService.upload(file, Long.valueOf(userId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long imageId,
            @AuthenticationPrincipal String userId
    ) {
        imageService.delete(imageId, Long.valueOf(userId));
        return ResponseEntity.noContent().build();
    }
}
