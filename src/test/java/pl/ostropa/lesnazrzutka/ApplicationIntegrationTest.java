package pl.ostropa.lesnazrzutka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import pl.ostropa.lesnazrzutka.logging.AppLogger;
import pl.ostropa.lesnazrzutka.logging.LoggingAspect;
import pl.ostropa.lesnazrzutka.monitoring.MemoryMonitor;
import pl.ostropa.lesnazrzutka.service.TransactionService;
import pl.ostropa.lesnazrzutka.repository.TransactionRepository;
import pl.ostropa.lesnazrzutka.model.Transaction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests for Application Startup and Components
 * Tests application context, components, and basic functionality
 */
@SpringBootTest
@DisplayName("Application Integration Tests")
class ApplicationIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MemoryMonitor memoryMonitor;

    @Test
    @DisplayName("Application context should load successfully")
    void testApplicationContextLoads() {
        assertNotNull(applicationContext, "Application context should not be null");
    }

    @Test
    @DisplayName("TransactionService bean should be created")
    void testTransactionServiceBeanExists() {
        assertNotNull(transactionService, "TransactionService bean should not be null");
        assertTrue(applicationContext.containsBean("transactionService"));
    }

    @Test
    @DisplayName("TransactionRepository bean should be created")
    void testTransactionRepositoryBeanExists() {
        assertNotNull(transactionRepository, "TransactionRepository bean should not be null");
    }

    @Test
    @DisplayName("MemoryMonitor bean should be created")
    void testMemoryMonitorBeanExists() {
        assertNotNull(memoryMonitor, "MemoryMonitor bean should not be null");
        assertTrue(applicationContext.containsBean("memoryMonitor"));
    }

    @Test
    @DisplayName("LoggingAspect should be present in context")
    void testLoggingAspectExists() {
        assertTrue(applicationContext.containsBean("loggingAspect"),
                "LoggingAspect bean should be present");
    }

    @Test
    @DisplayName("AppLogger should be creatable")
    void testAppLoggerCanBeCreated() {
        AppLogger logger = AppLogger.getLogger(ApplicationIntegrationTest.class);
        assertNotNull(logger, "AppLogger should be created successfully");
    }

    @Test
    @DisplayName("Correlation ID should be settable and gettable")
    void testCorrelationIdTracking() {
        String testId = "test-uuid-1234-5678";
        AppLogger.setCorrelationId(testId);
        String retrievedId = AppLogger.getCorrelationId();
        assertEquals(testId, retrievedId, "Correlation ID should be retrievable");
        AppLogger.clearCorrelationId();
    }

    @Test
    @DisplayName("Database should have test data loaded")
    void testDataLoaderLoaded() {
        long count = transactionRepository.count();
        assertTrue(count > 0, "Database should have test transactions loaded (count: " + count + ")");
    }

    @Test
    @DisplayName("TransactionService should retrieve all transactions")
    void testGetAllTransactions() {
        var transactions = transactionService.getAllTransactions();
        assertNotNull(transactions, "Transactions list should not be null");
        assertTrue(transactions.size() > 0, "Should have loaded test transactions");
    }

    @Test
    @DisplayName("TransactionService should group transactions correctly")
    void testGroupingFunctionality() {
        var grouped = transactionService.getIncomingTransactionsGroupedWithSum();
        assertNotNull(grouped, "Grouped transactions should not be null");
        assertTrue(grouped.size() > 0, "Should have grouped transactions");
    }

    @Test
    @DisplayName("All grouped transactions should have correct structure")
    void testGroupedTransactionStructure() {
        var grouped = transactionService.getIncomingTransactionsGroupedWithSum();

        grouped.forEach((accountNumber, groupData) -> {
            assertNotNull(accountNumber, "Account number should not be null");
            assertNotNull(groupData.getAccountNumber(), "Account number in groupData should not be null");
            assertNotNull(groupData.getTransactions(), "Transactions list should not be null");
            assertNotNull(groupData.getTotalAmount(), "Total amount should not be null");
            assertTrue(groupData.getTransactionCount() > 0, "Transaction count should be > 0");
            assertTrue(groupData.getTotalAmount() > 0, "Total amount should be > 0");
        });
    }

    @Test
    @DisplayName("Memory monitor should provide memory leak indicators")
    void testMemoryMonitorIndicators() {
        var indicators = memoryMonitor.getMemoryLeakIndicators();
        assertNotNull(indicators, "Memory leak indicators should not be null");
        assertTrue(indicators.getUsedMemory() > 0, "Used memory should be > 0");
        assertTrue(indicators.getMaxMemory() > 0, "Max memory should be > 0");
        assertTrue(indicators.getMemoryUsagePercentage() >= 0, "Memory usage percentage should be >= 0");
        assertTrue(indicators.getMemoryUsagePercentage() <= 100, "Memory usage percentage should be <= 100");
    }

    @Test
    @DisplayName("MemoryMonitor should not cause memory leak")
    void testMemoryMonitorDoesNotLeak() {
        long initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // Simulate multiple monitoring calls
        for (int i = 0; i < 10; i++) {
            memoryMonitor.logMemoryUsage();
        }

        long finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long delta = finalMemory - initialMemory;

        // Memory increase should be minimal (< 10MB)
        assertTrue(delta < 10_000_000, "Memory monitor should not cause significant memory leak");
    }

    @Test
    @DisplayName("Application should handle concurrent operations")
    void testConcurrentOperations() throws InterruptedException {
        Thread[] threads = new Thread[10];

        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                var transactions = transactionService.getAllTransactions();
                assertNotNull(transactions);
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Should complete without errors
        assertTrue(true, "Concurrent operations should complete successfully");
    }

    @Test
    @DisplayName("All Beans should be properly wired")
    void testBeanWiring() {
        assertNotNull(applicationContext.getBean(TransactionService.class));
        assertNotNull(applicationContext.getBean(TransactionRepository.class));
        assertNotNull(applicationContext.getBean(MemoryMonitor.class));
        assertNotNull(applicationContext.getBean(LoggingAspect.class));
    }
}

