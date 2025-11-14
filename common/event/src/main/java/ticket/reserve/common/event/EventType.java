package ticket.reserve.common.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ticket.reserve.common.event.payload.EventCreatedEventPayload;
import ticket.reserve.common.event.payload.PaymentConfirmedEventPayload;

@Getter
@RequiredArgsConstructor
public enum EventType {

    PAYMENT_CONFIRMED(PaymentConfirmedEventPayload.class, Topic.TICKET_RESERVE_PAYMENT),
    EVENT_CREATED(EventCreatedEventPayload.class, Topic.TICKET_RESERVE_EVENT)
    ;


    private final Class<? extends EventPayload> payloadClass;
    private final String topic;

    public static class Topic {
        public static final String TICKET_RESERVE_ADMIN = "ticket-reserve-admin";
        public static final String TICKET_RESERVE_EVENT = "ticket-reserve-event";
        public static final String TICKET_RESERVE_INVENTORY = "ticket-reserve-inventory";
        public static final String TICKET_RESERVE_PAYMENT = "ticket-reserve-payment";
        public static final String TICKET_RESERVE_RESERVATION = "ticket-reserve-reservation";
        public static final String TICKET_RESERVE_USER = "ticket-reserve-user";
    }
}
