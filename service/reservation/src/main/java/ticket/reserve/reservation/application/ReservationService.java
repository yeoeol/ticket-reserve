package ticket.reserve.reservation.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.reservation.application.port.out.InventoryPort;
import ticket.reserve.reservation.application.dto.request.InventoryHoldRequestDto;
import ticket.reserve.reservation.application.dto.request.ReservationRequestDto;
import ticket.reserve.reservation.application.dto.response.ReservationResponseDto;
import ticket.reserve.reservation.domain.Reservation;
import ticket.reserve.reservation.domain.repository.ReservationRepository;

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
            throw new RuntimeException("좌석 선점에 실패했습니다.");
        }

        Reservation reservation = request.toEntity(userId);
        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponseDto.of(savedReservation.getId(), savedReservation.getInventoryId(), savedReservation.getPrice());
    }

    @Transactional
    public void confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예매 Not Found"));
        reservation.confirm();

/*        InventoryConfirmRequestDto inventoryConfirmRequestDto =
                new InventoryConfirmRequestDto(reservation.getEventId(), reservation.getInventoryId());
        inventoryServiceClient.confirmInventory(inventoryConfirmRequestDto);*/
    }

    @Transactional
    public void releaseReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예매 Not Found"));
        reservation.release();
/*        InventoryReleaseRequestDto inventoryReleaseRequestDto =
                new InventoryReleaseRequestDto(reservation.getEventId(), reservation.getInventoryId());
        inventoryServiceClient.releaseInventory(inventoryReleaseRequestDto);*/
    }
}
