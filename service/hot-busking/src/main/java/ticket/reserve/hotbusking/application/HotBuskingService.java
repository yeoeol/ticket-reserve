package ticket.reserve.hotbusking.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ticket.reserve.hotbusking.application.dto.response.HotBuskingResponseDto;
import ticket.reserve.hotbusking.application.port.out.BuskingPort;
import ticket.reserve.hotbusking.application.port.out.BuskingSubscriptionCountPort;
import ticket.reserve.hotbusking.application.port.out.HotBuskingListPort;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotBuskingService {

    private final HotBuskingListPort hotBuskingListPort;
    private final BuskingSubscriptionCountPort buskingSubscriptionCountPort;
    private final BuskingPort buskingPort;

    public List<HotBuskingResponseDto> readAll() {
        return hotBuskingListPort.readAll().stream()
                .map(buskingPort::get)
                .filter(Objects::nonNull)
                .map(buskingResponseDto -> {
                    Long subscriptionCount = buskingSubscriptionCountPort.read(buskingResponseDto.id());
                    return HotBuskingResponseDto.from(buskingResponseDto, subscriptionCount);
                })
                .toList();
    }

    public void removeHotBuskingData(Long buskingId) {
        hotBuskingListPort.remove(buskingId);
        buskingSubscriptionCountPort.remove(buskingId);
    }
}
