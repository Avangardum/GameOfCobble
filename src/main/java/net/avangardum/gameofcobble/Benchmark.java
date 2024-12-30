package net.avangardum.gameofcobble;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.avangardum.gameofcobble.Util.assertNotNull;

final class Benchmark {
    private static final double NANOSECONDS_IN_MILLISECOND = 1_000_000;
    private static final double NANOSECONDS_IN_SECOND = 1_000_000_000;

    @NotNull private static final Map<String, Benchmark> instancesByName = new HashMap<>();
    @Nullable private Long startTimeNs;
    @NotNull private final Logger logger;
    private long minNs;
    private long maxNs;
    private double avgNs;
    private long count;
    @Nullable private Long firstStartTimeNs;

    private Benchmark(@NotNull String name) {
        logger = LogManager.getLogger(name);
    }

    public static @NotNull Benchmark get(@NotNull String name) {
        var existingInstance = instancesByName.get(name);
        if (existingInstance != null) return existingInstance;
        var newInstance = new Benchmark(name);
        instancesByName.put(name, newInstance);
        return newInstance;
    }

    public Benchmark start() {
        if (startTimeNs != null) throw new IllegalStateException("Benchmark is already started.");
        startTimeNs = System.nanoTime();
        if (firstStartTimeNs == null) firstStartTimeNs = startTimeNs;
        return this;
    }

    public void finish() {
        if (startTimeNs == null) throw new IllegalStateException("Benchmark is not yet started.");
        var resultNs = System.nanoTime() - startTimeNs;

        if (count == 0) {
            avgNs = minNs = maxNs = resultNs;
        } else {
            if (resultNs < minNs) minNs = resultNs;
            if (resultNs > maxNs) maxNs = resultNs;
            // This formula is chosen to avoid calculating the total sum to avoid possible integer overflow.
            avgNs = (double)avgNs * ((double)count / ((double)count + 1.0)) + (double)resultNs / ((double)count + 1.0);
        }
        count++;

        logResult(resultNs);
        startTimeNs = null;
    }

    private void logResult(long lastNs) {
        var lastMs = lastNs / NANOSECONDS_IN_MILLISECOND;
        var avgMs = avgNs / NANOSECONDS_IN_MILLISECOND;
        var minMs = minNs / NANOSECONDS_IN_MILLISECOND;
        var maxMs = maxNs / NANOSECONDS_IN_MILLISECOND;
        var nanosSinceFirstStart = System.nanoTime() - assertNotNull(firstStartTimeNs);
        var secondsSinceFirstStart = nanosSinceFirstStart / NANOSECONDS_IN_SECOND;
        var freqHz = count / secondsSinceFirstStart;
        logger.info(String.format("last: %.3fms, avg: %.3fms, min: %.3fms, max: %.3fms, freq: %.3fhz",
                lastMs, avgMs, minMs, maxMs, freqHz));
    }
}
