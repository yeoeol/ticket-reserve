package ticket.reserve.core.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ticket.reserve.core.event.payload.*;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {

    PAYMENT_CONFIRMED(PaymentConfirmedEventPayload.class, Topic.TICKET_RESERVE_PAYMENT),
    BUSKING_CREATED(BuskingCreatedEventPayload.class, Topic.TICKET_RESERVE_BUSKING),
    RESERVATION_EXPIRED(ReservationExpiredPayload.class, Topic.TICKET_RESERVE_RESERVATION),
    SUBSCRIPTION_NOTIFICATION_SENT(SubscriptionNotificationSentEventPayload.class, Topic.TICKET_RESERVE_SUBSCRIPTION),
    SUBSCRIPTION_CREATED(SubscriptionCreatedEventPayload.class, Topic.TICKET_RESERVE_SUBSCRIPTION),
    SUBSCRIPTION_CANCELLED(SubscriptionCancelledEventPayload.class, Topic.TICKET_RESERVE_SUBSCRIPTION),
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
        public static final String TICKET_RESERVE_SUBSCRIPTION = "ticket-reserve-subscription";
    }
}
