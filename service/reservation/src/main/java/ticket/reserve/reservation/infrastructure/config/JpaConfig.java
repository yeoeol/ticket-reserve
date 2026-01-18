package ticket.reserve.reservation.infrastructure.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EntityScan(basePackages = "ticket.reserve.reservation")
@EnableJpaRepositories(basePackages = "ticket.reserve.reservation")
public class JpaConfig {
}
