package ua.kpi.comsys.streams;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


public class SecurityLogAnalyzer {

    private static final String CRITICAL_LEVEL = "CRITICAL";

    public Optional<SecurityLog> findFirstCriticalEvent(Stream<SecurityLog> stream) {
        requireNonNullArgument(stream, "stream");
        return stream
                .filter(log -> CRITICAL_LEVEL.equals(log.severityLevel()))
                .findFirst();
    }

    public List<SecurityLog> getLogsBeforeDeadline(Stream<SecurityLog> stream, LocalDateTime deadline) {
        requireNonNullArgument(stream, "stream");
        requireNonNullArgument(deadline, "deadline");
        return stream
                .takeWhile(log -> log.timestamp().isBefore(deadline))
                .toList();
    }

    private static void requireNonNullArgument(Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
    }
}
