package ticket.reserve.reservation.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ticket.reserve.reservation.application.dto.request.InventoryHoldRequestDto;
import ticket.reserve.reservation.application.dto.request.ReservationRequestDto;
import ticket.reserve.reservation.application.dto.response.ReservationResponseDto;
import ticket.reserve.reservation.application.eventHandler.EventHandler;
import ticket.reserve.reservation.application.port.out.InventoryPort;
import ticket.reserve.reservation.domain.Reservation;
import ticket.reserve.reservation.domain.enums.ReservationStatus;
import ticket.reserve.reservation.domain.repository.ReservationRepository;
import ticket.reserve.tsid.IdGenerator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    ReservationService reservationService;

    @Mock ReservationRepository reservationRepository;
    @Mock InventoryPort inventoryPort;
    @Mock List<EventHandler> eventHandlers;
    @Mock IdGenerator idGenerator;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = Reservation.create(
                () -> 1234L, 1L, 1L, 1L, 5000
        );
    }

    @Test
    @DisplayName("예매 생성 성공 - 사용자ID, 이벤트ID, 좌석ID를 기반으로 예매 엔티티를 생성한다")
    void createReservationSuccess() {
        //given
        ReservationRequestDto request = new ReservationRequestDto(1L, 1L, 5000);
        given(reservationRepository.save(any(Reservation.class))).willReturn(reservation);

        //when
        ReservationResponseDto response = reservationService.createReservation(request, 1L);

        //then
        assertThat(response.reservationId()).isEqualTo(1234L);
        assertThat(response.inventoryId()).isEqualTo(request.inventoryId());
        assertThat(response.price()).isEqualTo(request.price());

        verify(inventoryPort, times(1)).holdInventory(any(InventoryHoldRequestDto.class));
    }
}