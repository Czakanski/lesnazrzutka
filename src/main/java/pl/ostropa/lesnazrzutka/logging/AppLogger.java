package pl.ostropa.lesnazrzutka.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import java.util.UUID;

/**
 * Custom Logger wrapper for annotating logs as BUSINESS or PERFORMANCE
 *
 * Usage:
 *   AppLogger logger = AppLogger.getLogger(MyClass.class);
 *
 *   // Business logs
 *   logger.business().info("User logged in: {}", username);
 *   logger.business().warn("Invalid transaction: {}", transactionId);
 *
 *   // Performance logs
 *   logger.performance().info("Query took {} ms", duration);
 *   logger.performance().debug("Cache hit rate: {}%", hitRate);
 *
 *   // Regular logs
 *   logger.info("Application started");
 *   logger.debug("Debug info");
 */
public class AppLogger {

    private static final String LOG_TYPE_KEY = "LOG_TYPE";
    private static final String BUSINESS = "BUSINESS";
    private static final String PERFORMANCE = "PERFORMANCE";
    private static final String CORRELATION_ID_KEY = "CORRELATION_ID";

    private final Logger logger;
    private final String className;

    private AppLogger(Logger logger, String className) {
        this.logger = logger;
        this.className = className;
    }

    /**
     * Get logger for class
     * @param clazz Class to log for
     * @return AppLogger instance
     */
    public static AppLogger getLogger(Class<?> clazz) {
        Logger slf4jLogger = LoggerFactory.getLogger(clazz);
        return new AppLogger(slf4jLogger, clazz.getSimpleName());
    }

    /**
     * Get logger for string name
     * @param name Logger name
     * @return AppLogger instance
     */
    public static AppLogger getLogger(String name) {
        Logger slf4jLogger = LoggerFactory.getLogger(name);
        return new AppLogger(slf4jLogger, name);
    }

    /**
     * Get business logger - for application business logic logs
     * Tags: [BUSINESS]
     * Example: "User transaction completed", "Order processed"
     * @return BusinessLogger instance
     */
    public BusinessLogger business() {
        return new BusinessLogger(logger, className);
    }

    /**
     * Get performance logger - for performance metrics and timing
     * Tags: [PERFORMANCE]
     * Example: "Database query took 45ms", "API response time 200ms"
     * @return PerformanceLogger instance
     */
    public PerformanceLogger performance() {
        return new PerformanceLogger(logger, className);
    }

    /**
     * Get security logger - for security-related events
     * Tags: [SECURITY]
     * Example: "Authentication failed", "Authorization denied"
     * @return SecurityLogger instance
     */
    public SecurityLogger security() {
        return new SecurityLogger(logger, className);
    }

    /**
     * Set correlation ID for request tracing
     * @param correlationId Unique ID for request tracking
     */
    public static void setCorrelationId(String correlationId) {
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        MDC.put(CORRELATION_ID_KEY, correlationId);
    }

    /**
     * Get current correlation ID
     * @return Current correlation ID or null
     */
    public static String getCorrelationId() {
        return MDC.get(CORRELATION_ID_KEY);
    }

    /**
     * Clear correlation ID
     */
    public static void clearCorrelationId() {
        MDC.remove(CORRELATION_ID_KEY);
    }

    // Regular logging methods (delegated to SLF4J)

    public void trace(String message) {
        logger.trace(message);
    }

    public void trace(String format, Object... arguments) {
        logger.trace(format, arguments);
    }

    public void debug(String message) {
        logger.debug(message);
    }

    public void debug(String format, Object... arguments) {
        logger.debug(format, arguments);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void info(String format, Object... arguments) {
        logger.info(format, arguments);
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void warn(String format, Object... arguments) {
        logger.warn(format, arguments);
    }

    public void warn(String message, Throwable throwable) {
        logger.warn(message, throwable);
    }

    public void error(String message) {
        logger.error(message);
    }

    public void error(String format, Object... arguments) {
        logger.error(format, arguments);
    }

    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    // Inner classes for typed loggers

    /**
     * Business Logger - for business logic events
     */
    public static class BusinessLogger {
        private final Logger logger;
        private final String className;

        private BusinessLogger(Logger logger, String className) {
            this.logger = logger;
            this.className = className;
        }

        private void logWithType(String level, String message) {
            MDC.put(LOG_TYPE_KEY, BUSINESS);
            try {
                switch (level) {
                    case "TRACE" -> logger.trace("[BUSINESS] {}", message);
                    case "DEBUG" -> logger.debug("[BUSINESS] {}", message);
                    case "INFO" -> logger.info("[BUSINESS] {}", message);
                    case "WARN" -> logger.warn("[BUSINESS] {}", message);
                    case "ERROR" -> logger.error("[BUSINESS] {}", message);
                }
            } finally {
                MDC.remove(LOG_TYPE_KEY);
            }
        }

        private void logWithType(String level, String format, Object... arguments) {
            MDC.put(LOG_TYPE_KEY, BUSINESS);
            try {
                String message = "[BUSINESS] " + format;
                switch (level) {
                    case "TRACE" -> logger.trace(message, arguments);
                    case "DEBUG" -> logger.debug(message, arguments);
                    case "INFO" -> logger.info(message, arguments);
                    case "WARN" -> logger.warn(message, arguments);
                    case "ERROR" -> logger.error(message, arguments);
                }
            } finally {
                MDC.remove(LOG_TYPE_KEY);
            }
        }

        public void trace(String message) {
            logWithType("TRACE", message);
        }

        public void trace(String format, Object... arguments) {
            logWithType("TRACE", format, arguments);
        }

        public void debug(String message) {
            logWithType("DEBUG", message);
        }

        public void debug(String format, Object... arguments) {
            logWithType("DEBUG", format, arguments);
        }

        public void info(String message) {
            logWithType("INFO", message);
        }

        public void info(String format, Object... arguments) {
            logWithType("INFO", format, arguments);
        }

        public void warn(String message) {
            logWithType("WARN", message);
        }

        public void warn(String format, Object... arguments) {
            logWithType("WARN", format, arguments);
        }

        public void warn(String message, Throwable throwable) {
            MDC.put(LOG_TYPE_KEY, BUSINESS);
            try {
                logger.warn("[BUSINESS] " + message, throwable);
            } finally {
                MDC.remove(LOG_TYPE_KEY);
            }
        }

        public void error(String message) {
            logWithType("ERROR", message);
        }

        public void error(String format, Object... arguments) {
            logWithType("ERROR", format, arguments);
        }

        public void error(String message, Throwable throwable) {
            MDC.put(LOG_TYPE_KEY, BUSINESS);
            try {
                logger.error("[BUSINESS] " + message, throwable);
            } finally {
                MDC.remove(LOG_TYPE_KEY);
            }
        }
    }

    /**
     * Performance Logger - for performance metrics
     */
    public static class PerformanceLogger {
        private final Logger logger;
        private final String className;

        private PerformanceLogger(Logger logger, String className) {
            this.logger = logger;
            this.className = className;
        }

        private void logWithType(String level, String message) {
            MDC.put(LOG_TYPE_KEY, PERFORMANCE);
            try {
                switch (level) {
                    case "TRACE" -> logger.trace("[PERFORMANCE] {}", message);
                    case "DEBUG" -> logger.debug("[PERFORMANCE] {}", message);
                    case "INFO" -> logger.info("[PERFORMANCE] {}", message);
                    case "WARN" -> logger.warn("[PERFORMANCE] {}", message);
                    case "ERROR" -> logger.error("[PERFORMANCE] {}", message);
                }
            } finally {
                MDC.remove(LOG_TYPE_KEY);
            }
        }

        private void logWithType(String level, String format, Object... arguments) {
            MDC.put(LOG_TYPE_KEY, PERFORMANCE);
            try {
                String message = "[PERFORMANCE] " + format;
                switch (level) {
                    case "TRACE" -> logger.trace(message, arguments);
                    case "DEBUG" -> logger.debug(message, arguments);
                    case "INFO" -> logger.info(message, arguments);
                    case "WARN" -> logger.warn(message, arguments);
                    case "ERROR" -> logger.error(message, arguments);
                }
            } finally {
                MDC.remove(LOG_TYPE_KEY);
            }
        }

        public void trace(String message) {
            logWithType("TRACE", message);
        }

        public void trace(String format, Object... arguments) {
            logWithType("TRACE", format, arguments);
        }

        public void debug(String message) {
            logWithType("DEBUG", message);
        }

        public void debug(String format, Object... arguments) {
            logWithType("DEBUG", format, arguments);
        }

        public void info(String message) {
            logWithType("INFO", message);
        }

        public void info(String format, Object... arguments) {
            logWithType("INFO", format, arguments);
        }

        public void warn(String message) {
            logWithType("WARN", message);
        }

        public void warn(String format, Object... arguments) {
            logWithType("WARN", format, arguments);
        }

        public void warn(String message, Throwable throwable) {
            MDC.put(LOG_TYPE_KEY, PERFORMANCE);
            try {
                logger.warn("[PERFORMANCE] " + message, throwable);
            } finally {
                MDC.remove(LOG_TYPE_KEY);
            }
        }

        public void error(String message) {
            logWithType("ERROR", message);
        }

        public void error(String format, Object... arguments) {
            logWithType("ERROR", format, arguments);
        }

        public void error(String message, Throwable throwable) {
            MDC.put(LOG_TYPE_KEY, PERFORMANCE);
            try {
                logger.error("[PERFORMANCE] " + message, throwable);
            } finally {
                MDC.remove(LOG_TYPE_KEY);
            }
        }

        /**
         * Log timing information
         * @param operation Operation name
         * @param durationMs Duration in milliseconds
         * @param success Whether operation succeeded
         */
        public void logTiming(String operation, long durationMs, boolean success) {
            String status = success ? "✓" : "✗";
            info("[TIMING] {} - {} ms {}", operation, durationMs, status);
        }

        /**
         * Log query performance
         * @param query Query description
         * @param durationMs Query duration
         * @param rowCount Number of rows returned
         */
        public void logQuery(String query, long durationMs, int rowCount) {
            info("[QUERY] {} - {} ms - {} rows", query, durationMs, rowCount);
        }
    }

    /**
     * Security Logger - for security events
     */
    public static class SecurityLogger {
        private final Logger logger;
        private final String className;

        private SecurityLogger(Logger logger, String className) {
            this.logger = logger;
            this.className = className;
        }

        private void logWithType(String level, String message) {
            MDC.put(LOG_TYPE_KEY, "SECURITY");
            try {
                switch (level) {
                    case "TRACE" -> logger.trace("[SECURITY] {}", message);
                    case "DEBUG" -> logger.debug("[SECURITY] {}", message);
                    case "INFO" -> logger.info("[SECURITY] {}", message);
                    case "WARN" -> logger.warn("[SECURITY] {}", message);
                    case "ERROR" -> logger.error("[SECURITY] {}", message);
                }
            } finally {
                MDC.remove(LOG_TYPE_KEY);
            }
        }

        private void logWithType(String level, String format, Object... arguments) {
            MDC.put(LOG_TYPE_KEY, "SECURITY");
            try {
                String message = "[SECURITY] " + format;
                switch (level) {
                    case "TRACE" -> logger.trace(message, arguments);
                    case "DEBUG" -> logger.debug(message, arguments);
                    case "INFO" -> logger.info(message, arguments);
                    case "WARN" -> logger.warn(message, arguments);
                    case "ERROR" -> logger.error(message, arguments);
                }
            } finally {
                MDC.remove(LOG_TYPE_KEY);
            }
        }

        public void info(String message) {
            logWithType("INFO", message);
        }

        public void info(String format, Object... arguments) {
            logWithType("INFO", format, arguments);
        }

        public void warn(String message) {
            logWithType("WARN", message);
        }

        public void warn(String format, Object... arguments) {
            logWithType("WARN", format, arguments);
        }

        public void error(String message) {
            logWithType("ERROR", message);
        }

        public void error(String format, Object... arguments) {
            logWithType("ERROR", format, arguments);
        }
    }
}

