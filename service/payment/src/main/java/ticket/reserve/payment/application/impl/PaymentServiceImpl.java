package ticket.reserve.payment.application.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.tsid.IdGenerator;
import ticket.reserve.payment.application.PaymentPublishService;
import ticket.reserve.payment.application.PaymentService;
import ticket.reserve.payment.application.dto.request.PaymentConfirmRequestDto;
import ticket.reserve.payment.application.dto.response.TossResponseDto;
import ticket.reserve.payment.application.port.out.TossPaymentsPort;
import ticket.reserve.payment.domain.Payment;
import ticket.reserve.payment.domain.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final IdGenerator idGenerator;
    private final PaymentRepository paymentRepository;
    private final TossPaymentsPort tossPaymentsPort;
    private final PaymentPublishService paymentPublishService;

    @Transactional
    public void createPayment(String orderId, Long userId, Long reservationId, Long inventoryId) {
        Payment payment = Payment.create(
                idGenerator,
                userId,
                reservationId,
                inventoryId,
                orderId
        );
        paymentRepository.save(payment);
    }

    public void confirmPayment(PaymentConfirmRequestDto request) {
        TossResponseDto tossResponseDto = tossPaymentsPort.confirmPayment(request);
        paymentPublishService.updateInfoAndpublishPaymentConfirmedEvent(tossResponseDto);
    }
}
