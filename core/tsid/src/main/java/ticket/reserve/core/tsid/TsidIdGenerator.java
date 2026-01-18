package ticket.reserve.core.tsid;

import io.hypersistence.tsid.TSID;
import org.springframework.stereotype.Component;

@Component
public class TsidIdGenerator implements IdGenerator{
    @Override
    public Long nextId() {
        return TSID.Factory.getTsid().toLong();
    }
}
