package ticket.reserve.payment.presentation.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import ticket.reserve.payment.application.dto.request.PaymentConfirmRequestDto;
import ticket.reserve.payment.application.PaymentService;
import ticket.reserve.payment.application.dto.request.PaymentPageRequest;
import ticket.reserve.payment.application.port.out.PaymentPropertiesPort;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentPropertiesPort paymentPropertiesPort;

    @GetMapping
    public String paymentPage(
            @Valid @ModelAttribute PaymentPageRequest request,
            Model model
    ) {
        String orderId = UUID.randomUUID().toString().substring(0, 12);

        model.addAttribute("userId", request.userId());
        model.addAttribute("reservationId", request.reservationId());
        model.addAttribute("amount", request.amount());
        model.addAttribute("orderId", orderId);
        model.addAttribute("orderName", "티켓 예매");
        model.addAttribute("clientKey", paymentPropertiesPort.getClientKey());
        model.addAttribute("gatewayUrl", paymentPropertiesPort.getGatewayUrl());

        paymentService.createPayment(orderId, request.userId(), request.reservationId(), request.inventoryId());
        return "payment";
    }

    @GetMapping("/success")
    public String paymentSuccess(@Valid @ModelAttribute PaymentConfirmRequestDto request) {
        paymentService.confirmPayment(request);
        return "redirect:/";
    }
}
