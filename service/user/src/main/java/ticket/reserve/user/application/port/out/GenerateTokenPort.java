package ticket.reserve.user.application.port.out;

import java.util.List;

public interface GenerateTokenPort {
    String generateToken(Long userId, List<String> userRoles);
    long getRemainingTime(String jwt);
}
