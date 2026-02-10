package ticket.reserve.user.application.port.out;

public interface LocationPort {
    void addLocation(Long userId, Double latitude, Double longitude);
}
