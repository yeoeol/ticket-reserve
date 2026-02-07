package ticket.reserve.busking.application.port.out;

public interface SubscriptionPort {
    Boolean isSubscribe(Long buskingId, Long userId);
}
