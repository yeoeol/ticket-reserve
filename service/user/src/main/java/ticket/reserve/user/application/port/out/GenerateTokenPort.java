package ticket.reserve.user.application.port.out;

import java.util.Set;

public interface GenerateTokenPort {
    String generateToken(Long userId, Set<String> userRoles);
}
