package ticket.reserve.reservation.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ticket.reserve.core.event.Event;
import ticket.reserve.core.event.EventPayload;
import ticket.reserve.global.exception.CustomException;
import ticket.reserve.global.exception.ErrorCode;
import ticket.reserve.reservation.application.eventHandler.EventHandler;
import ticket.reserve.reservation.application.port.out.InventoryPort;
import ticket.reserve.reservation.application.dto.request.InventoryHoldRequestDto;
import ticket.reserve.reservation.application.dto.request.ReservationRequestDto;
import ticket.reserve.reservation.application.dto.response.ReservationResponseDto;
import ticket.reserve.reservation.domain.Reservation;
import ticket.reserve.reservation.domain.repository.ReservationRepository;
import ticket.reserve.reservation.global.annotation.AllowedUser;
import ticket.reserve.core.tsid.IdGenerator;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final InventoryPort inventoryPort;
    private final List<EventHandler> eventHandlers;
    private final IdGenerator idGenerator;

    @AllowedUser(buskingId = "#request.buskingId", userId = "#userId")
    @Transactional
    public ReservationResponseDto createReservation(ReservationRequestDto request, Long userId) {
        try {
            InventoryHoldRequestDto holdRequest = new InventoryHoldRequestDto(request.buskingId(), request.inventoryId());
            // 좌석 선점 로직
            inventoryPort.holdInventory(holdRequest);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVENTORY_HOLD_FAIL);
        }

        Reservation reservation = request.toEntity(idGenerator, userId);
        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponseDto.of(savedReservation.getId(), savedReservation.getInventoryId(), savedReservation.getPrice());
    }

    public void handleEvent(Event<EventPayload> event) {
        EventHandler<EventPayload> eventHandler = findEventHandler(event);
        if (eventHandler == null) {
            return;
        }

        eventHandler.handle(event);
    }

    private EventHandler<EventPayload> findEventHandler(Event<EventPayload> event) {
        return eventHandlers.stream()
                .filter(eventHandler -> eventHandler.supports(event))
                .findAny()
                .orElse(null);
    }
}
