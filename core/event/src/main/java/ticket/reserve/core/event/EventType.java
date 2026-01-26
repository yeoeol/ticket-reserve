package ticket.reserve.core.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ticket.reserve.core.event.payload.BuskingCreatedEventPayload;
import ticket.reserve.core.event.payload.PaymentConfirmedEventPayload;
import ticket.reserve.core.event.payload.ReservationExpiredPayload;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {

    PAYMENT_CONFIRMED(PaymentConfirmedEventPayload.class, Topic.TICKET_RESERVE_PAYMENT),
    EVENT_CREATED(BuskingCreatedEventPayload.class, Topic.TICKET_RESERVE_BUSKING),
    RESERVATION_EXPIRED(ReservationExpiredPayload.class, Topic.TICKET_RESERVE_RESERVATION)
    ;

    private final Class<? extends EventPayload> payloadClass;
    private final String topic;

    public static EventType from(String type) {
        try {
            return valueOf(type);
        } catch (Exception e) {
            log.error("[EventType.from] type={}", type, e);
            return null;
        }
    }

    public static class Topic {
        public static final String TICKET_RESERVE_ADMIN = "ticket-reserve-admin";
        public static final String TICKET_RESERVE_BUSKING = "ticket-reserve-busking";
        public static final String TICKET_RESERVE_INVENTORY = "ticket-reserve-inventory";
        public static final String TICKET_RESERVE_PAYMENT = "ticket-reserve-payment";
        public static final String TICKET_RESERVE_RESERVATION = "ticket-reserve-reservation";
        public static final String TICKET_RESERVE_USER = "ticket-reserve-user";
    }
}
