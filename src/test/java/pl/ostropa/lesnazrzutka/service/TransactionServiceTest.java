package pl.ostropa.lesnazrzutka.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.ostropa.lesnazrzutka.model.Transaction;
import pl.ostropa.lesnazrzutka.repository.TransactionRepository;
import pl.ostropa.lesnazrzutka.service.TransactionService.TransactionGroupData;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TransactionService Tests")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Test transaction 1 - from account 1
        transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setFromAccountNumber("PL61106000760000636213110001");
        transaction1.setToAccountNumber("PL12345678901234567890123456");
        transaction1.setAmount(1500.00);
        transaction1.setTransactionDate(LocalDateTime.now().minusDays(10));
        transaction1.setTransactionType("WPŁATA");
        transaction1.setDescription("Wpłata wynagrodzenia");
        transaction1.setReference("SALARY001");
        transaction1.setCurrency("PLN");

        // Test transaction 2 - from account 1
        transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setFromAccountNumber("PL61106000760000636213110001");
        transaction2.setToAccountNumber("PL12345678901234567890123456");
        transaction2.setAmount(2500.00);
        transaction2.setTransactionDate(LocalDateTime.now().minusDays(5));
        transaction2.setTransactionType("WPŁATA");
        transaction2.setDescription("Wpłata bonus");
        transaction2.setReference("BONUS001");
        transaction2.setCurrency("PLN");

        // Test transaction 3 - from account 2
        transaction3 = new Transaction();
        transaction3.setId(3L);
        transaction3.setFromAccountNumber("PL72114020040000300201355387");
        transaction3.setToAccountNumber("PL12345678901234567890123456");
        transaction3.setAmount(5000.00);
        transaction3.setTransactionDate(LocalDateTime.now().minusDays(8));
        transaction3.setTransactionType("WPŁATA");
        transaction3.setDescription("Wpłata od klienta");
        transaction3.setReference("INV001");
        transaction3.setCurrency("PLN");
    }

    @Test
    @DisplayName("Should save transaction successfully")
    void testSaveTransaction() {
        when(transactionRepository.save(transaction1)).thenReturn(transaction1);

        Transaction result = transactionService.saveTransaction(transaction1);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1500.00, result.getAmount());
        verify(transactionRepository, times(1)).save(transaction1);
    }

    @Test
    @DisplayName("Should get transaction by ID")
    void testGetTransactionById() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction1));

        Transaction result = transactionService.getTransactionById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PL61106000760000636213110001", result.getFromAccountNumber());
    }

    @Test
    @DisplayName("Should get all transactions")
    void testGetAllTransactions() {
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2, transaction3);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.getAllTransactions();

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get transactions by from account number")
    void testGetTransactionsByFromAccount() {
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        when(transactionRepository.findTransactionsFromAccount("PL61106000760000636213110001"))
                .thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionsByFromAccount("PL61106000760000636213110001");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getFromAccountNumber().equals("PL61106000760000636213110001")));
    }

    @Test
    @DisplayName("Should calculate sum of incoming transactions by account")
    void testGetIncomingTransactionsSumByAccount() {
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        when(transactionRepository.findTransactionsFromAccount("PL61106000760000636213110001"))
                .thenReturn(transactions);

        Double sum = transactionService.getIncomingTransactionsSumByAccount("PL61106000760000636213110001");

        assertEquals(4000.00, sum);
    }

    @Test
    @DisplayName("Should group transactions by source account")
    void testGetIncomingTransactionsGroupedBySourceAccount() {
        List<Transaction> allTransactions = Arrays.asList(transaction1, transaction2, transaction3);
        when(transactionRepository.findAll()).thenReturn(allTransactions);

        Map<String, List<Transaction>> result = transactionService.getIncomingTransactionsGroupedBySourceAccount();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey("PL61106000760000636213110001"));
        assertTrue(result.containsKey("PL72114020040000300201355387"));
        assertEquals(2, result.get("PL61106000760000636213110001").size());
        assertEquals(1, result.get("PL72114020040000300201355387").size());
    }

    @Test
    @DisplayName("Should group transactions with sum")
    void testGetIncomingTransactionsGroupedWithSum() {
        List<Transaction> allTransactions = Arrays.asList(transaction1, transaction2, transaction3);
        when(transactionRepository.findAll()).thenReturn(allTransactions);

        Map<String, TransactionGroupData> result = transactionService.getIncomingTransactionsGroupedWithSum();

        assertNotNull(result);
        assertEquals(2, result.size());

        TransactionGroupData group1 = result.get("PL61106000760000636213110001");
        assertNotNull(group1);
        assertEquals("PL61106000760000636213110001", group1.getAccountNumber());
        assertEquals(4000.00, group1.getTotalAmount());
        assertEquals(2, group1.getTransactionCount());

        TransactionGroupData group2 = result.get("PL72114020040000300201355387");
        assertNotNull(group2);
        assertEquals("PL72114020040000300201355387", group2.getAccountNumber());
        assertEquals(5000.00, group2.getTotalAmount());
        assertEquals(1, group2.getTransactionCount());
    }

    @Test
    @DisplayName("Should delete transaction")
    void testDeleteTransaction() {
        transactionService.deleteTransaction(1L);

        verify(transactionRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should get transactions by type")
    void testGetTransactionsByType() {
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2, transaction3);
        when(transactionRepository.findByTransactionType("WPŁATA")).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionsByType("WPŁATA");

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(t -> t.getTransactionType().equals("WPŁATA")));
    }

    @Test
    @DisplayName("Should handle empty transaction list")
    void testEmptyTransactionList() {
        when(transactionRepository.findAll()).thenReturn(Arrays.asList());

        List<Transaction> result = transactionService.getAllTransactions();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}

