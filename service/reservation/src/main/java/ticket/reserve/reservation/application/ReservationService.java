package ticket.reserve.reservation.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.reservation.application.port.out.InventoryPort;
import ticket.reserve.reservation.application.dto.request.InventoryHoldRequestDto;
import ticket.reserve.reservation.application.dto.request.ReservationRequestDto;
import ticket.reserve.reservation.application.dto.response.ReservationResponseDto;
import ticket.reserve.reservation.domain.Reservation;
import ticket.reserve.reservation.domain.enums.ReservationStatus;
import ticket.reserve.reservation.domain.repository.ReservationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final InventoryPort inventoryPort;

    @Transactional
    public ReservationResponseDto createReservation(ReservationRequestDto request, Long userId) {
        try {
            InventoryHoldRequestDto holdRequest = new InventoryHoldRequestDto(request.eventId(), request.inventoryId());
            // 좌석 선점 로직
            inventoryPort.holdInventory(holdRequest);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVENTORY_HOLD_FAIL);
        }

        Reservation reservation = request.toEntity(userId);
        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponseDto.of(savedReservation.getId(), savedReservation.getInventoryId(), savedReservation.getPrice());
    }

    @Transactional
    public void confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findByIdForUpdate(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (isValidRequest(reservation)) return;

        reservation.confirm();
    }

    @Transactional
    public void releaseReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findByIdForUpdate(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        reservation.release();
    }

    private static boolean isValidRequest(Reservation reservation) {
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            log.error("결제는 성공했으나 예매 시간 초과로 취소되었습니다. 환불 로직이 필요합니다. reservationId={}", reservation.getId());
            return true;
        }
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            return true;
        }
        return false;
    }
}
