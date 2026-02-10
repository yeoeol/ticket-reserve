package ticket.reserve.user.application.port.out;

public interface TokenStorePort {
    void addBlackList(String token, long ttl);
}
