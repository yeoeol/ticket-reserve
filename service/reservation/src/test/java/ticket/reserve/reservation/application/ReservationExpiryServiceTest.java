package ticket.reserve.reservation.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.common.event.EventType;
import ticket.reserve.common.event.payload.ReservationExpiredPayload;
import ticket.reserve.common.outboxmessagerelay.OutboxEventPublisher;
import ticket.reserve.reservation.domain.Reservation;
import ticket.reserve.reservation.domain.enums.ReservationStatus;
import ticket.reserve.reservation.domain.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ReservationExpiryServiceTest {

    @InjectMocks
    ReservationExpiryService reservationExpiryService;

    @Mock ReservationRepository reservationRepository;
    @Mock OutboxEventPublisher outboxEventPublisher;

    @Test
    @DisplayName("만료된 예매 정리 - 5분 이상 경과된 PENDING 예매가 있으면 이벤트를 발행한다")
    void findAndPublishExpiredReservationsSuccess() {
        //given
        Reservation reservation1 = createReservation(1L, 1L);
        Reservation reservation2 = createReservation(2L, 1L);
        Reservation reservation3 = createReservation(3L, 1L);
        List<Reservation> expiredReservations = List.of(reservation1, reservation2, reservation3);

        given(reservationRepository.findAllByStatusAndCreatedAtBefore(eq(ReservationStatus.PENDING), any()))
                .willReturn(expiredReservations);

        //when
        reservationExpiryService.findAndPublishExpiredReservations();

        //then
        verify(reservationRepository, times(1))
                .findAllByStatusAndCreatedAtBefore(eq(ReservationStatus.PENDING), any());
        verify(outboxEventPublisher, times(3))
                .publish(eq(EventType.RESERVATION_EXPIRED), any(ReservationExpiredPayload.class), anyLong());
    }

    @Test
    @DisplayName("만료된 예매 정리 - 만료된 예매가 없으면 이벤트를 발행하지 않는다")
    void findAndNotPublishExpiredReservations() {
        //given
        given(reservationRepository.findAllByStatusAndCreatedAtBefore(eq(ReservationStatus.PENDING), any()))
                .willReturn(List.of());

        //when
        reservationExpiryService.findAndPublishExpiredReservations();

        //then
        verify(reservationRepository, times(1))
                .findAllByStatusAndCreatedAtBefore(eq(ReservationStatus.PENDING), any());
        verify(outboxEventPublisher, never())
                .publish(any(), any(), anyLong());
    }

    private Reservation createReservation(Long id, Long buskingId) {
        return Reservation.create(
                () -> id, 1L, buskingId, 50L, 5000
        );
    }
}