package ua.kpi.comsys.streams;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityLogAnalyzerInfiniteStreamTest {

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 10, 21, 12, 0);

    private final SecurityLogAnalyzer analyzer = new SecurityLogAnalyzer();

    @Test
    @Timeout(2)
    void findFirstCriticalEventStopsOnInfiniteStream() {

        Stream<SecurityLog> infinite = Stream.iterate(0, i -> i + 1)
                .map(i -> new SecurityLog(
                        "10.0.0." + i,
                        i == 4 ? "CRITICAL" : "INFO",
                        "event " + i,
                        BASE.plusSeconds(i)));

        Optional<String> ip = analyzer.findFirstCriticalEvent(infinite).map(SecurityLog::ipAddress);

        assertEquals(Optional.of("10.0.0.4"), ip);
    }

    @Test
    @Timeout(2)
    void getLogsBeforeDeadlineStopsOnInfiniteStream() {

        Stream<SecurityLog> infinite = Stream.iterate(BASE, t -> t.plusMinutes(1))
                .map(t -> new SecurityLog("10.0.0.1", "INFO", "tick", t));

        List<SecurityLog> result = analyzer.getLogsBeforeDeadline(infinite, BASE.plusMinutes(3));

        assertEquals(3, result.size());
        assertEquals(BASE.plusMinutes(2), result.get(2).timestamp());
    }

    @Test
    @Timeout(2)
    void detectIpBurstsStopsOnInfiniteStreamAfterLimitReached() {

        Stream<SecurityLog> infinite = Stream.iterate(0, i -> i + 1)
                .map(i -> new SecurityLog(
                        (i / 3) % 2 == 0 ? "1.1.1.1" : "2.2.2.2",
                        "INFO",
                        "burst event",
                        BASE.plusSeconds(i)));

        List<String> result = analyzer.detectIpBursts(infinite, 3, 2);

        assertEquals(List.of("1.1.1.1", "2.2.2.2"), result);
    }

    @Test
    @Timeout(2)
    void detectIpBurstsWithGenerateStopsWhenEnoughBursts() {

        AtomicInteger counter = new AtomicInteger();
        Stream<SecurityLog> infinite = Stream.generate(() -> {
            int n = counter.getAndIncrement();
            return new SecurityLog("10.0.0." + (n / 2), "INFO", "burst event", BASE.plusSeconds(n));
        });

        List<String> result = analyzer.detectIpBursts(infinite, 2, 3);

        assertEquals(List.of("10.0.0.0", "10.0.0.1", "10.0.0.2"), result);
    }
}
