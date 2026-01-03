package ticket.reserve.payment.presentation.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ticket.reserve.payment.application.dto.request.PaymentConfirmRequestDto;
import ticket.reserve.payment.application.PaymentService;
import ticket.reserve.payment.infrastructure.config.PaymentProperties;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentProperties paymentProperties;

    @GetMapping
    public String paymentPage(
            @RequestParam Long userId,
            @RequestParam Long reservationId,
            @RequestParam Long inventoryId,
            @RequestParam int amount,
            Model model
    ) {
        String orderId = UUID.randomUUID().toString().substring(0, 12);

        model.addAttribute("userId", userId);
        model.addAttribute("reservationId", reservationId);
        model.addAttribute("amount", amount);
        model.addAttribute("orderId", orderId);
        model.addAttribute("orderName", "티켓 예매");
        model.addAttribute("clientKey", paymentProperties.getClientKey());

        paymentService.createPayment(orderId, userId, reservationId, inventoryId);
        return "payment";
    }

    @GetMapping("/success")
    public String paymentSuccess(@ModelAttribute PaymentConfirmRequestDto request) {
        paymentService.confirmPayment(request);
        return "redirect:/";
    }
}
