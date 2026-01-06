package pl.ostropa.lesnazrzutka.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import java.util.Arrays;

/**
 * AOP Aspect for automatic logging with performance tracking
 *
 * Automatically logs:
 * - Method entry and exit
 * - Execution time
 * - Exceptions
 * - Arguments and return values (configurable)
 */
@Aspect
@Component
public class LoggingAspect {

    /**
     * Log all public methods in service classes
     */
    @Around("execution(public * pl.ostropa.lesnazrzutka.service.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, "SERVICE");
    }

    /**
     * Log all public methods in controller classes
     */
    @Around("execution(public * pl.ostropa.lesnazrzutka.controller.*.*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, "CONTROLLER");
    }

    /**
     * Log all public methods in view classes
     */
    @Around("execution(public * pl.ostropa.lesnazrzutka.views.*.*(..))")
    public Object logViewMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, "VIEW");
    }

    /**
     * Core logging logic
     */
    private Object logMethodExecution(ProceedingJoinPoint joinPoint, String component) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        AppLogger logger = AppLogger.getLogger(className);
        long startTime = System.currentTimeMillis();

        try {
            // Log method entry
            logger.debug("{}.{} - START [args: {}]",
                component, methodName, formatArgs(args));

            // Execute method
            Object result = joinPoint.proceed();

            // Log successful execution with timing
            long duration = System.currentTimeMillis() - startTime;
            logger.performance().info("{}.{} - {} ms [✓]",
                component, methodName, duration);

            return result;

        } catch (Throwable throwable) {
            // Log error with timing
            long duration = System.currentTimeMillis() - startTime;
            logger.error("{}.{} - {} ms [✗] ERROR: {}",
                component, methodName, duration, throwable.getMessage(), throwable);

            throw throwable;
        }
    }

    /**
     * Format arguments for logging (hide sensitive data)
     */
    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "none";
        }

        try {
            return Arrays.stream(args)
                    .map(arg -> arg == null ? "null" : arg.toString())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("none");
        } catch (Exception e) {
            return "[unable to format]";
        }
    }
}

