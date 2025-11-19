package ticket.reserve.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {
        "ticket.reserve.global",
        "ticket.reserve.event"
})
public class EventApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventApplication.class);
    }
}
