package ua.kpi.comsys.streams;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Gatherer;
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

    public List<String> detectIpBursts(Stream<SecurityLog> stream, int burstThreshold, int maxIpsToReport) {
        requireNonNullArgument(stream, "stream");
        if (burstThreshold <= 0) {
            throw new IllegalArgumentException("burstThreshold must be positive, but was " + burstThreshold);
        }
        if (maxIpsToReport <= 0) {
            throw new IllegalArgumentException("maxIpsToReport must be positive, but was " + maxIpsToReport);
        }
        return stream
                .gather(burstDetector(burstThreshold, maxIpsToReport))
                .toList();
    }

    private static Gatherer<SecurityLog, BurstState, String> burstDetector(int burstThreshold, int maxIpsToReport) {
        return Gatherer.ofSequential(
                BurstState::new,
                (BurstState state, SecurityLog log, Gatherer.Downstream<? super String> downstream) -> {
                    String ip = log.ipAddress();

                    if (Objects.equals(ip, state.currentIp)) {
                        state.streakLength++;
                    } else {
                        state.currentIp = ip;
                        state.streakLength = 1;
                    }

                    boolean reachedThreshold = state.streakLength == burstThreshold;
                    if (reachedThreshold && state.reportedIps.add(ip)) {
                        state.report.add(ip);
                        boolean downstreamWantsMore = downstream.push(ip);
                        return downstreamWantsMore && state.report.size() < maxIpsToReport;
                    }
                    return true;
                }
        );
    }

    private static final class BurstState {
        private String currentIp;
        private int streakLength;
        private final Set<String> reportedIps = new HashSet<>();
        private final List<String> report = new ArrayList<>();
    }

    private static void requireNonNullArgument(Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
    }
}
