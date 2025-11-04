package ticket.reserve.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ticket.reserve.payment.client.ReservationServiceClient;
import ticket.reserve.payment.dto.PaymentConfirmRequestDto;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ReservationServiceClient reservationServiceClient;

    public void confirmPayment(PaymentConfirmRequestDto request) {
        // PG사 Mock API 호출 - 결제 승인 로직

        boolean paymentSuccess = true;  // 응답 결과

        if (paymentSuccess) {
            reservationServiceClient.confirmReservation(request.reservationId());
        }
        else {
            reservationServiceClient.releaseReservation(request.reservationId());
        }
    }
}
