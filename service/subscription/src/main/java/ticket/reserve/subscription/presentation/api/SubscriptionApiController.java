package ticket.reserve.subscription.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ticket.reserve.subscription.application.SubscriptionQueryService;
import ticket.reserve.subscription.application.SubscriptionService;
import ticket.reserve.subscription.application.dto.request.IsSubscribeRequestDto;
import ticket.reserve.subscription.application.dto.request.SubscriptionCancelRequestDto;
import ticket.reserve.subscription.application.dto.request.SubscriptionRequestDto;
import ticket.reserve.subscription.application.dto.response.BuskingResponseDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
public class SubscriptionApiController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionQueryService subscriptionQueryService;

    @PostMapping
    public ResponseEntity<Void> subscribe(@Valid @RequestBody SubscriptionRequestDto request) {
        subscriptionService.subscribe(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<Void> unsubscribe(@Valid @RequestBody SubscriptionCancelRequestDto request) {
        subscriptionService.unsubscribe(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Boolean> isSubscribe(
            @Valid @ModelAttribute IsSubscribeRequestDto request
    ) {
        return ResponseEntity.ok(subscriptionQueryService.isSubscriptionActive(request.buskingId(), request.userId()));
    }

    @GetMapping("/me")
    public ResponseEntity<List<BuskingResponseDto>> getSubscriptionList(
            @AuthenticationPrincipal String userId
    ) {
        return ResponseEntity.ok(subscriptionService.getAllByUserId(Long.valueOf(userId)));
    }
}
