package ticket.reserve.reservation.application.port.out;

import ticket.reserve.common.event.payload.ReservationExpiredPayload;

public interface ReservationPublishPort {
    void expireReservation(ReservationExpiredPayload payload);
}
