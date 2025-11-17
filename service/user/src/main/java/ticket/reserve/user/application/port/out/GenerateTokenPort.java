package ticket.reserve.user.application.port.out;

public interface GenerateTokenPort {
    String generateToken(Long userId, String role);
}
