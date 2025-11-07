package ticket.reserve.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ticket.reserve.payment.config.PaymentProperties;
import ticket.reserve.payment.service.PaymentService;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentProperties paymentProperties;

    @GetMapping("/payments")
    public String paymentPage(@RequestParam Long userId,
                              @RequestParam Long reservationId,
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

        paymentService.createPayment(orderId, userId, reservationId);
        return "payment";
    }
}
