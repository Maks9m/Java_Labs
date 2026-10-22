package ua.kpi.comsys.streams;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SecurityLogAnalyzerTest {

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 10, 22, 12, 0);

    private final SecurityLogAnalyzer analyzer = new SecurityLogAnalyzer();

    private static SecurityLog log(String ip, String level, LocalDateTime timestamp) {
        return new SecurityLog(ip, level, "event from " + ip, timestamp);
    }

    // ---------- findFirstCriticalEvent ----------

    @Test
    void findFirstCriticalEventReturnsFirstCriticalEvent() {
        SecurityLog firstCritical = log("10.0.0.2", "CRITICAL", BASE.plusSeconds(1));
        Stream<SecurityLog> stream = Stream.of(
                log("10.0.0.1", "INFO", BASE),
                firstCritical,
                log("10.0.0.3", "CRITICAL", BASE.plusSeconds(2))
        );

        assertEquals(Optional.of(firstCritical), analyzer.findFirstCriticalEvent(stream));
    }

    @Test
    void findFirstCriticalEventIsCaseSensitive() {
        Stream<SecurityLog> stream = Stream.of(
                log("10.0.0.1", "critical", BASE),
                log("10.0.0.2", "Critical", BASE.plusSeconds(1))
        );

        assertEquals(Optional.empty(), analyzer.findFirstCriticalEvent(stream));
    }

    @Test
    void findFirstCriticalEventOnEmptyStreamReturnsEmpty() {
        assertEquals(Optional.empty(), analyzer.findFirstCriticalEvent(Stream.empty()));
    }

    @Test
    void findFirstCriticalEventDoesNotFailOnNullSeverity() {
        Stream<SecurityLog> stream = Stream.of(log("10.0.0.1", null, BASE));

        assertEquals(Optional.empty(), analyzer.findFirstCriticalEvent(stream));
    }

    @Test
    void findFirstCriticalEventRejectsNullStream() {
        assertThrows(IllegalArgumentException.class,
                () -> analyzer.findFirstCriticalEvent(null));
    }

    // ---------- getLogsBeforeDeadline ----------

    @Test
    void getLogsBeforeDeadlineExcludesEventAtDeadline() {
        SecurityLog first = log("10.0.0.1", "INFO", BASE);
        SecurityLog second = log("10.0.0.2", "INFO", BASE.plusSeconds(1));
        SecurityLog atDeadline = log("10.0.0.3", "INFO", BASE.plusSeconds(2));
        SecurityLog afterDeadline = log("10.0.0.4", "INFO", BASE.plusSeconds(3));
        Stream<SecurityLog> stream = Stream.of(first, second, atDeadline, afterDeadline);

        List<SecurityLog> result = analyzer.getLogsBeforeDeadline(stream, BASE.plusSeconds(2));

        assertEquals(List.of(first, second), result);
    }

    @Test
    void getLogsBeforeDeadlineReturnsEmptyListWhenDeadlineIsBeforeAllEvents() {
        Stream<SecurityLog> stream = Stream.of(log("10.0.0.1", "INFO", BASE));

        assertEquals(List.of(), analyzer.getLogsBeforeDeadline(stream, BASE.minusSeconds(1)));
    }

    @Test
    void getLogsBeforeDeadlineOnEmptyStreamReturnsEmptyList() {
        assertEquals(List.of(), analyzer.getLogsBeforeDeadline(Stream.empty(), BASE));
    }

    @Test
    void getLogsBeforeDeadlineRejectsNullArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> analyzer.getLogsBeforeDeadline(null, BASE));
        assertThrows(IllegalArgumentException.class,
                () -> analyzer.getLogsBeforeDeadline(Stream.empty(), null));
    }

    // ---------- detectIpBursts ----------

    @Test
    void detectIpBurstsRejectsInvalidArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> analyzer.detectIpBursts(null, 3, 5));
        assertThrows(IllegalArgumentException.class,
                () -> analyzer.detectIpBursts(Stream.empty(), 0, 5));
        assertThrows(IllegalArgumentException.class,
                () -> analyzer.detectIpBursts(Stream.empty(), -1, 5));
        assertThrows(IllegalArgumentException.class,
                () -> analyzer.detectIpBursts(Stream.empty(), 3, 0));
    }

    @Test
    void detectIpBurstsIgnoresSeriesInterruptedByOtherIp() {
        Stream<SecurityLog> stream = Stream.of(
                log("10.0.0.1", "INFO", BASE),
                log("10.0.0.1", "INFO", BASE.plusSeconds(1)),
                log("10.0.0.2", "INFO", BASE.plusSeconds(2)),
                log("10.0.0.1", "INFO", BASE.plusSeconds(3)),
                log("10.0.0.1", "INFO", BASE.plusSeconds(4))
        );

        assertEquals(List.of(), analyzer.detectIpBursts(stream, 3, 5));
    }

    @Test
    void detectIpBurstsReportsIpWhenThresholdIsReached() {
        Stream<SecurityLog> stream = Stream.of(
                log("10.0.0.1", "INFO", BASE),
                log("10.0.0.1", "INFO", BASE.plusSeconds(1)),
                log("10.0.0.1", "INFO", BASE.plusSeconds(2)),
                log("10.0.0.1", "INFO", BASE.plusSeconds(3)),
                log("10.0.0.2", "INFO", BASE.plusSeconds(4))
        );

        assertEquals(List.of("10.0.0.1"), analyzer.detectIpBursts(stream, 3, 5));
    }

    @Test
    void detectIpBurstsReportsEachIpOnlyOnce() {
        Stream<SecurityLog> stream = Stream.of(
                log("10.0.0.1", "INFO", BASE),
                log("10.0.0.1", "INFO", BASE.plusSeconds(1)),
                log("10.0.0.1", "INFO", BASE.plusSeconds(2)),
                log("10.0.0.2", "INFO", BASE.plusSeconds(3)),
                log("10.0.0.1", "INFO", BASE.plusSeconds(4)),
                log("10.0.0.1", "INFO", BASE.plusSeconds(5)),
                log("10.0.0.1", "INFO", BASE.plusSeconds(6))
        );

        assertEquals(List.of("10.0.0.1"), analyzer.detectIpBursts(stream, 3, 5));
    }

    @Test
    void detectIpBurstsStopsAtMaxIpsToReport() {
        Stream<SecurityLog> stream = Stream.of(
                log("10.0.0.1", "INFO", BASE),
                log("10.0.0.1", "INFO", BASE.plusSeconds(1)),
                log("10.0.0.2", "INFO", BASE.plusSeconds(2)),
                log("10.0.0.2", "INFO", BASE.plusSeconds(3)),
                log("10.0.0.3", "INFO", BASE.plusSeconds(4)),
                log("10.0.0.3", "INFO", BASE.plusSeconds(5))
        );

        assertEquals(List.of("10.0.0.1", "10.0.0.2"), analyzer.detectIpBursts(stream, 2, 2));
    }

    @Test
    void detectIpBurstsDoesNotLeakStateBetweenCalls() {
        SecurityLog first = log("10.0.0.1", "INFO", BASE);
        SecurityLog second = log("10.0.0.1", "INFO", BASE.plusSeconds(1));

        // Перший виклик залишає серію довжиною 2 (поріг 3). Другий виклик має почати з нуля.
        assertEquals(List.of(), analyzer.detectIpBursts(Stream.of(first, second), 3, 5));
        assertEquals(List.of(), analyzer.detectIpBursts(Stream.of(first), 3, 5));
    }
}
