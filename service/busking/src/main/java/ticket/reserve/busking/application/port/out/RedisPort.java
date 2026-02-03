package ticket.reserve.busking.application.port.out;

public interface RedisPort {
    boolean isSubscribed(Long buskingId, Long userId);
}
