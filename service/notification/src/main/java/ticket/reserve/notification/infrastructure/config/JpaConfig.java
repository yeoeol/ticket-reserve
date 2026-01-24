package ticket.reserve.notification.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@RequiredArgsConstructor
@EntityScan(basePackages = "ticket.reserve.notification")
@EnableJpaRepositories(basePackages = "ticket.reserve.notification")
public class JpaConfig {

}
