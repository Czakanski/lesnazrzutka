package pl.ostropa.lesnazrzutka.monitoring;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.ostropa.lesnazrzutka.logging.AppLogger;
import java.lang.management.*;

/**
 * Memory and Performance Monitoring Component
 * Tracks memory usage, GC events, and performance metrics
 *
 * OPTIMIZATION: Helps detect memory leaks and performance issues
 * LOGGING: Uses AppLogger with performance tracking
 */
@Component
public class MemoryMonitor {

    private static final AppLogger logger = AppLogger.getLogger(MemoryMonitor.class);

    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private final RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
    private long lastMemoryCheck = 0;
    private long lastGCCount = 0;

    /**
     * Monitor memory usage every 60 seconds
     */
    @Scheduled(fixedRate = 60000)
    public void logMemoryUsage() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            long maxMemory = runtime.maxMemory();
            long usedPercentage = (usedMemory * 100) / maxMemory;

            // Log memory metrics (performance)
            logger.performance().info("Memory Status: {} MB / {} MB ({}%)",
                    formatBytes(usedMemory),
                    formatBytes(maxMemory),
                    usedPercentage);

            // Check for memory leak (continuous growth)
            if (lastMemoryCheck > 0) {
                long delta = usedMemory - lastMemoryCheck;
                if (delta > 50_000_000) { // 50 MB growth
                    logger.warn("⚠️ HIGH MEMORY GROWTH: {} MB in 60 seconds", formatBytes(delta));
                }
            }
            lastMemoryCheck = usedMemory;

            // Log heap memory details
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
            logger.debug("Heap - Committed: {} MB, Used: {} MB, Max: {} MB",
                    formatBytes(heapUsage.getCommitted()),
                    formatBytes(heapUsage.getUsed()),
                    formatBytes(heapUsage.getMax()));

        } catch (Exception e) {
            logger.error("Error monitoring memory", e);
        }
    }

    /**
     * Monitor GC activity every 60 seconds
     */
    @Scheduled(fixedRate = 60000)
    public void logGCActivity() {
        try {
            long totalGCCount = 0;
            long totalGCTime = 0;

            for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
                long count = gcBean.getCollectionCount();
                long time = gcBean.getCollectionTime();
                totalGCCount += count;
                totalGCTime += time;

                if (gcBean.isValid()) {
                    logger.debug("GC {} - Collections: {}, Time: {} ms",
                            gcBean.getName(),
                            count,
                            time);
                }
            }

            // Alert on high GC frequency
            if (lastGCCount > 0) {
                long gcCountDelta = totalGCCount - lastGCCount;
                if (gcCountDelta > 30) { // More than 30 GC events per minute
                    logger.warn("⚠️ HIGH GC FREQUENCY: {} collections in 60 seconds", gcCountDelta);
                }
            }

            logger.performance().info("GC Status - Total Collections: {}, Total Time: {} ms",
                    totalGCCount,
                    totalGCTime);

            lastGCCount = totalGCCount;

        } catch (Exception e) {
            logger.error("Error monitoring GC", e);
        }
    }

    /**
     * Monitor thread count (detect thread leaks)
     */
    @Scheduled(fixedRate = 300000)  // Every 5 minutes
    public void logThreadMetrics() {
        try {
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            int threadCount = threadBean.getThreadCount();
            int peakThreadCount = threadBean.getPeakThreadCount();
            long totalStartedThreadCount = threadBean.getTotalStartedThreadCount();

            logger.performance().info("Thread Status - Active: {}, Peak: {}, Total Started: {}",
                    threadCount,
                    peakThreadCount,
                    totalStartedThreadCount);

            // Alert on thread leak (growing thread count)
            if (threadCount > 200) {
                logger.warn("⚠️ HIGH THREAD COUNT: {} active threads", threadCount);
            }

        } catch (Exception e) {
            logger.error("Error monitoring threads", e);
        }
    }

    /**
     * Monitor system load (CPU)
     */
    @Scheduled(fixedRate = 120000)  // Every 2 minutes
    public void logSystemMetrics() {
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            double systemLoad = osBean.getSystemLoadAverage();
            double processCpuUsage = osBean.getProcessCpuUsage();
            int availableProcessors = osBean.getAvailableProcessors();

            logger.performance().info("System Status - CPU Usage: {}%, System Load: {}, Processors: {}",
                    String.format("%.2f", processCpuUsage * 100),
                    String.format("%.2f", systemLoad),
                    availableProcessors);

            // Alert on high CPU
            if (processCpuUsage > 0.8) {
                logger.warn("⚠️ HIGH CPU USAGE: {}%", String.format("%.2f", processCpuUsage * 100));
            }

        } catch (Exception e) {
            logger.error("Error monitoring system", e);
        }
    }

    /**
     * Get memory leak indicators
     * @return MemoryLeakIndicators object
     */
    public MemoryLeakIndicators getMemoryLeakIndicators() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();

        MemoryLeakIndicators indicators = new MemoryLeakIndicators();
        indicators.setUsedMemory(usedMemory);
        indicators.setMaxMemory(maxMemory);
        indicators.setMemoryUsagePercentage((usedMemory * 100) / maxMemory);
        indicators.setAvailableMemory(runtime.freeMemory());

        // Get GC metrics
        long totalGCCount = ManagementFactory.getGarbageCollectorMXBeans().stream()
                .mapToLong(GarbageCollectorMXBean::getCollectionCount)
                .sum();
        indicators.setTotalGCCount(totalGCCount);

        return indicators;
    }

    /**
     * Force garbage collection (use cautiously!)
     */
    public void forceGC() {
        logger.warn("⚠️ Forcing Garbage Collection - This may cause performance impact");
        System.gc();
    }

    /**
     * Format bytes to human readable format
     */
    private String formatBytes(long bytes) {
        if (bytes <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return String.format("%.1f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    /**
     * Data class for memory leak indicators
     */
    public static class MemoryLeakIndicators {
        private long usedMemory;
        private long maxMemory;
        private long availableMemory;
        private long memoryUsagePercentage;
        private long totalGCCount;

        // Getters and Setters
        public long getUsedMemory() { return usedMemory; }
        public void setUsedMemory(long usedMemory) { this.usedMemory = usedMemory; }

        public long getMaxMemory() { return maxMemory; }
        public void setMaxMemory(long maxMemory) { this.maxMemory = maxMemory; }

        public long getAvailableMemory() { return availableMemory; }
        public void setAvailableMemory(long availableMemory) { this.availableMemory = availableMemory; }

        public long getMemoryUsagePercentage() { return memoryUsagePercentage; }
        public void setMemoryUsagePercentage(long memoryUsagePercentage) { this.memoryUsagePercentage = memoryUsagePercentage; }

        public long getTotalGCCount() { return totalGCCount; }
        public void setTotalGCCount(long totalGCCount) { this.totalGCCount = totalGCCount; }
    }
}

