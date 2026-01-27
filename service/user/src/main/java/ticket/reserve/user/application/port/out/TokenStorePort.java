package ticket.reserve.user.application.port.out;

public interface TokenStorePort {
    void addBlackList(String token, long ttl);

    void addLocation(Long userId, Double latitude, Double longitude);
}
