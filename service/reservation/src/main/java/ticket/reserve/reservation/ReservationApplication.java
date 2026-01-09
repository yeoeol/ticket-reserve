package ticket.reserve.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@EnableDiscoveryClient
@EntityScan(basePackages = {"ticket.reserve"})
@EnableJpaRepositories(basePackages = "ticket.reserve")
@SpringBootApplication
public class ReservationApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReservationApplication.class);
    }
}
