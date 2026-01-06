package pl.ostropa.lesnazrzutka.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.ostropa.lesnazrzutka.model.Transaction;
import pl.ostropa.lesnazrzutka.service.TransactionService;
import pl.ostropa.lesnazrzutka.service.TransactionService.TransactionGroupData;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Pobiera wszystkie transakcje
     */
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    /**
     * Pobiera transakcję po ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        if (transaction != null) {
            return ResponseEntity.ok(transaction);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Tworzy nową transakcję
     */
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.saveTransaction(transaction));
    }

    /**
     * Aktualizuje istniejącą transakcję
     */
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long id,
            @RequestBody Transaction transaction) {
        Transaction existing = transactionService.getTransactionById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        transaction.setId(id);
        return ResponseEntity.ok(transactionService.saveTransaction(transaction));
    }

    /**
     * Usuwa transakcję
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id);
        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Pobiera transakcje dla danego konta źródłowego
     */
    @GetMapping("/from/{accountNumber}")
    public ResponseEntity<List<Transaction>> getTransactionsFromAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(transactionService.getTransactionsByFromAccount(accountNumber));
    }

    /**
     * Pobiera transakcje dla danego konta docelowego
     */
    @GetMapping("/to/{accountNumber}")
    public ResponseEntity<List<Transaction>> getTransactionsToAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(transactionService.getTransactionsByToAccount(accountNumber));
    }

    /**
     * Pobiera sumę wpłat dla danego konta źródłowego
     */
    @GetMapping("/sum/{accountNumber}")
    public ResponseEntity<Double> getIncomingTransactionSum(
            @PathVariable String accountNumber) {
        Double sum = transactionService.getIncomingTransactionsSumByAccount(accountNumber);
        return ResponseEntity.ok(sum);
    }

    /**
     * Pobiera wszystkie wpłaty pogrupowane po koncie źródłowym z sumami
     */
    @GetMapping("/grouped/all")
    public ResponseEntity<Map<String, TransactionGroupData>> getGroupedTransactions() {
        return ResponseEntity.ok(transactionService.getIncomingTransactionsGroupedWithSum());
    }

    /**
     * Pobiera transakcje według typu
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Transaction>> getTransactionsByType(
            @PathVariable String type) {
        return ResponseEntity.ok(transactionService.getTransactionsByType(type));
    }
}

