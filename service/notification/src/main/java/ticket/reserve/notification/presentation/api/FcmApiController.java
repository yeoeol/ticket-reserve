package ticket.reserve.notification.presentation.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ticket.reserve.notification.application.FcmTokenService;
import ticket.reserve.notification.application.dto.request.FcmTokenRequestDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FcmApiController {

    private final FcmTokenService fcmTokenService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody FcmTokenRequestDto request) {
        fcmTokenService.saveOrUpdate(request);
        return ResponseEntity.noContent().build();
    }

}
