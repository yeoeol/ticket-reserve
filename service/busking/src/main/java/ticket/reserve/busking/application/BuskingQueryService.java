package ticket.reserve.busking.application;

import ticket.reserve.busking.domain.busking.Busking;

import java.util.List;

public interface BuskingQueryService {
    Busking findById(Long id);

    void delete(Long id);

    List<Long> getIds();
}
