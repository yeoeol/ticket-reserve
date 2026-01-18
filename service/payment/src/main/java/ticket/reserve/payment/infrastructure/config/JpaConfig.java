package ticket.reserve.payment.infrastructure.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "ticket.reserve.payment")
@EnableJpaRepositories(basePackages = "ticket.reserve.payment")
public class JpaConfig {
}
