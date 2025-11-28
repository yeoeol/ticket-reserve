package ticket.reserve.reservation.infrastucture.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "EVENT-SERVICE")
public interface EventServiceClient {


}
