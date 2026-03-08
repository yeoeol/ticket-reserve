package ticket.reserve.core.log.context;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TraceContextManager {

    private final Tracer tracer;
    private final Propagator propagator;

    private static final Propagator.Setter<Map<String, String>> MAP_SETTER =
            (carrier, key, value) -> carrier.put(key, value);
    private static final Propagator.Getter<Map<String, String>> MAP_GETTER =
            (carrier, key) -> carrier.get(key);

    /**
     * 현재 스레드의 Trace 정보를 Map으로 추출 (Inject 역할)
     */
    public Map<String, String> captureCurrentContext() {
        Map<String, String> carrier = new HashMap<>();
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            propagator.inject(currentSpan.context(), carrier, MAP_SETTER);
        }
        return carrier;
    }

    /**
     * 전달받은 Map 정보를 바탕으로 새로운 Span 범위 내에서 로직 실행 (Extract 역할)
     */
    public void runWithContext(Map<String, String> carrier, String spanName, Runnable action) {
        Span.Builder spanBuilder = propagator.extract(carrier, MAP_GETTER);
        Span restoredSpan = spanBuilder.name(spanName).start();

        try (Tracer.SpanInScope ignored = tracer.withSpan(restoredSpan)) {
            action.run();
        } finally {
            restoredSpan.end();
        }
    }
}
