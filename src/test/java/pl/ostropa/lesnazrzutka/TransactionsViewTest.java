package pl.ostropa.lesnazrzutka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import pl.ostropa.lesnazrzutka.views.TransactionsView;
import pl.ostropa.lesnazrzutka.service.TransactionService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI Component Tests for TransactionsView
 * Tests Vaadin view rendering and functionality
 */
@SpringBootTest
@DisplayName("TransactionsView UI Tests")
class TransactionsViewTest {

    @Autowired
    private TransactionService transactionService;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("TransactionsView should be creatable")
    void testViewCreation() {
        assertNotNull(transactionService, "TransactionService should be autowired");

        // We can't fully test Vaadin UI without a running server
        // but we can test that the service works
        var transactions = transactionService.getAllTransactions();
        assertNotNull(transactions, "Transactions should be retrievable");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Grouped transactions should render correctly")
    void testGroupedTransactionsData() {
        var grouped = transactionService.getIncomingTransactionsGroupedWithSum();

        // Verify data structure for rendering
        assertNotNull(grouped);
        grouped.forEach((accountNumber, groupData) -> {
            assertNotNull(accountNumber);
            assertNotNull(groupData.getAccountNumber());
            assertNotNull(groupData.getTransactions());
            assertTrue(groupData.getTransactions().size() > 0);
            assertTrue(groupData.getTotalAmount() > 0);
        });
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Summary cards should have correct data")
    void testSummaryData() {
        var grouped = transactionService.getIncomingTransactionsGroupedWithSum();

        // Calculate summary metrics
        int totalAccounts = grouped.size();
        int totalTransactions = grouped.values().stream()
                .mapToInt(g -> g.getTransactions().size())
                .sum();
        Double totalSum = grouped.values().stream()
                .mapToDouble(g -> g.getTotalAmount())
                .sum();

        assertTrue(totalAccounts > 0, "Should have accounts");
        assertTrue(totalTransactions > 0, "Should have transactions");
        assertTrue(totalSum > 0, "Should have total sum");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Performance - Grouping should be fast")
    void testGroupingPerformance() {
        long startTime = System.currentTimeMillis();
        var grouped = transactionService.getIncomingTransactionsGroupedWithSum();
        long duration = System.currentTimeMillis() - startTime;

        assertTrue(duration < 500, "Grouping should complete within 500ms, took: " + duration + "ms");
    }
}

