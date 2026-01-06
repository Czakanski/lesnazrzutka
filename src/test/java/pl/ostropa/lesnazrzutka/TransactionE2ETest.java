package pl.ostropa.lesnazrzutka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.ostropa.lesnazrzutka.model.Transaction;
import pl.ostropa.lesnazrzutka.service.TransactionService;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * End-to-End Tests for Transactions Module
 * Tests full API endpoints with authentication
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Transaction API E2E Tests")
class TransactionE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Setup test data if needed
    }

    // ============== GET TESTS ==============

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("GET /api/transactions - Should return all transactions")
    void testGetAllTransactions() throws Exception {
        mockMvc.perform(get("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$[*].id").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("GET /api/transactions/{id} - Should return transaction by ID")
    void testGetTransactionById() throws Exception {
        // First, get all transactions to find an ID
        MvcResult result = mockMvc.perform(get("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        if (!content.equals("[]")) {
            mockMvc.perform(get("/api/transactions/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("GET /api/transactions/grouped/all - Should return grouped transactions")
    void testGetGroupedTransactions() throws Exception {
        mockMvc.perform(get("/api/transactions/grouped/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.Map.class)));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("GET /api/transactions/from/{account} - Should return transactions from account")
    void testGetTransactionsFromAccount() throws Exception {
        String accountNumber = "PL61106000760000636213110001";
        mockMvc.perform(get("/api/transactions/from/" + accountNumber)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("GET /api/transactions/sum/{account} - Should return transaction sum")
    void testGetTransactionSum() throws Exception {
        String accountNumber = "PL61106000760000636213110001";
        mockMvc.perform(get("/api/transactions/sum/" + accountNumber)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ============== POST TESTS ==============

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("POST /api/transactions - Should create new transaction")
    void testCreateTransaction() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setFromAccountNumber("PL11111111111111111111111111");
        transaction.setToAccountNumber("PL12345678901234567890123456");
        transaction.setAmount(1000.00);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType("WPŁATA");
        transaction.setDescription("Test Transaction");
        transaction.setReference("TEST001");
        transaction.setCurrency("PLN");

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.fromAccountNumber").value("PL11111111111111111111111111"))
                .andExpect(jsonPath("$.amount").value(1000.00));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("POST /api/transactions - Should validate required fields")
    void testCreateTransactionWithMissingFields() throws Exception {
        String invalidTransaction = "{}";

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidTransaction))
                .andExpect(status().isBadRequest());
    }

    // ============== PUT TESTS ==============

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("PUT /api/transactions/{id} - Should update transaction")
    void testUpdateTransaction() throws Exception {
        // Create transaction first
        Transaction transaction = new Transaction();
        transaction.setFromAccountNumber("PL11111111111111111111111111");
        transaction.setToAccountNumber("PL12345678901234567890123456");
        transaction.setAmount(500.00);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType("WPŁATA");
        transaction.setDescription("Original");
        transaction.setReference("UPDATE001");
        transaction.setCurrency("PLN");
        transaction.setCreatedDate(LocalDateTime.now());

        Transaction saved = transactionService.saveTransaction(transaction);

        // Update it
        transaction.setAmount(1500.00);
        transaction.setDescription("Updated");

        mockMvc.perform(put("/api/transactions/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(1500.00))
                .andExpect(jsonPath("$.description").value("Updated"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("PUT /api/transactions/{id} - Should return 404 for non-existent transaction")
    void testUpdateNonExistentTransaction() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setFromAccountNumber("PL11111111111111111111111111");
        transaction.setToAccountNumber("PL12345678901234567890123456");
        transaction.setAmount(500.00);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType("WPŁATA");
        transaction.setDescription("Test");
        transaction.setReference("NOEXIST");
        transaction.setCurrency("PLN");

        mockMvc.perform(put("/api/transactions/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isNotFound());
    }

    // ============== DELETE TESTS ==============

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("DELETE /api/transactions/{id} - Should delete transaction")
    void testDeleteTransaction() throws Exception {
        // Create transaction first
        Transaction transaction = new Transaction();
        transaction.setFromAccountNumber("PL11111111111111111111111111");
        transaction.setToAccountNumber("PL12345678901234567890123456");
        transaction.setAmount(500.00);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType("WPŁATA");
        transaction.setDescription("To Delete");
        transaction.setReference("DELETE001");
        transaction.setCurrency("PLN");
        transaction.setCreatedDate(LocalDateTime.now());

        Transaction saved = transactionService.saveTransaction(transaction);

        mockMvc.perform(delete("/api/transactions/" + saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("DELETE /api/transactions/{id} - Should return 404 for non-existent transaction")
    void testDeleteNonExistentTransaction() throws Exception {
        mockMvc.perform(delete("/api/transactions/99999"))
                .andExpect(status().isNotFound());
    }

    // ============== SECURITY TESTS ==============

    @Test
    @DisplayName("GET /api/transactions - Should require authentication")
    void testGetTransactionsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("GET /api/transactions - Should allow authenticated users")
    void testGetTransactionsWithAuth() throws Exception {
        mockMvc.perform(get("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ============== GROUPING TESTS ==============

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Grouped transactions should have correct structure")
    void testGroupedTransactionsStructure() throws Exception {
        mockMvc.perform(get("/api/transactions/grouped/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*[0].accountNumber").exists())
                .andExpect(jsonPath("$.*[0].totalAmount").isNumber())
                .andExpect(jsonPath("$.*[0].transactionCount").isNumber())
                .andExpect(jsonPath("$.*[0].transactions").isArray());
    }

    // ============== PERFORMANCE TESTS ==============

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("GET /api/transactions should respond within 1000ms")
    void testTransactionsResponseTime() throws Exception {
        long startTime = System.currentTimeMillis();

        mockMvc.perform(get("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        long duration = System.currentTimeMillis() - startTime;
        assert duration < 1000 : "Response took " + duration + "ms, expected < 1000ms";
    }

    // ============== INTEGRATION TESTS ==============

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Full transaction lifecycle - Create, Read, Update, Delete")
    void testFullTransactionLifecycle() throws Exception {
        // 1. CREATE
        Transaction transaction = new Transaction();
        transaction.setFromAccountNumber("PL99999999999999999999999999");
        transaction.setToAccountNumber("PL12345678901234567890123456");
        transaction.setAmount(2500.00);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType("WPŁATA");
        transaction.setDescription("Lifecycle Test");
        transaction.setReference("LIFECYCLE001");
        transaction.setCurrency("PLN");

        MvcResult createResult = mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk())
                .andReturn();

        String createdContent = createResult.getResponse().getContentAsString();
        Transaction createdTransaction = objectMapper.readValue(createdContent, Transaction.class);
        Long transactionId = createdTransaction.getId();

        // 2. READ
        mockMvc.perform(get("/api/transactions/" + transactionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId.intValue()));

        // 3. UPDATE
        createdTransaction.setAmount(3000.00);
        mockMvc.perform(put("/api/transactions/" + transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdTransaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(3000.00));

        // 4. DELETE
        mockMvc.perform(delete("/api/transactions/" + transactionId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Verify test data is loaded correctly")
    void testDataLoaderWorked() throws Exception {
        mockMvc.perform(get("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }
}

