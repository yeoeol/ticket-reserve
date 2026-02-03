package ticket.reserve.subscription.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ticket.reserve.subscription.application.SubscriptionService;
import ticket.reserve.subscription.application.dto.request.SubscriptionRequestDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
public class SubscriptionApiController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<Void> subscribe(@Valid @RequestBody SubscriptionRequestDto request) {
        subscriptionService.subscribe(request);
        return ResponseEntity.noContent().build();
    }
}
