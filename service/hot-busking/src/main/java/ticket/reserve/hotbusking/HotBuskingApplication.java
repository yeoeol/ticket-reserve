package ticket.reserve.hotbusking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class HotBuskingApplication {
    public static void main(String[] args) {
        SpringApplication.run(HotBuskingApplication.class, args);
    }
}
