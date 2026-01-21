package ticket.reserve.core.inbox;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "ticket.reserve.core.inbox")
@EntityScan(basePackages = "ticket.reserve.core.inbox")
@EnableJpaRepositories(basePackages = "ticket.reserve.core.inbox")
public class InboxModuleConfig {
}
