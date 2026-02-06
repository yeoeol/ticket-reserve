package ticket.reserve.busking.application.port.out;

import ticket.reserve.busking.application.dto.request.IsSubscribeRequestDto;

public interface SubscriptionPort {
    Boolean isSubscribe(IsSubscribeRequestDto request);
}
