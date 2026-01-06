package pl.ostropa.lesnazrzutka.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ostropa.lesnazrzutka.model.Transaction;
import pl.ostropa.lesnazrzutka.repository.TransactionRepository;
import pl.ostropa.lesnazrzutka.logging.AppLogger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private static final AppLogger logger = AppLogger.getLogger(TransactionService.class);

    public Transaction saveTransaction(Transaction transaction) {
        long startTime = System.currentTimeMillis();
        try {
            Transaction saved = transactionRepository.save(transaction);
            long duration = System.currentTimeMillis() - startTime;

            logger.business().info("Transaction saved - From: {}, Amount: {}, Duration: {} ms",
                    saved.getFromAccountNumber(),
                    saved.getAmount(),
                    duration);
            logger.performance().logTiming("saveTransaction", duration, true);

            return saved;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Error saving transaction", e);
            logger.performance().logTiming("saveTransaction", duration, false);
            throw e;
        }
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    public List<Transaction> getAllTransactions() {
        long startTime = System.currentTimeMillis();
        List<Transaction> transactions = transactionRepository.findAll();
        long duration = System.currentTimeMillis() - startTime;

        logger.performance().logQuery("SELECT ALL FROM Transaction", duration, transactions.size());

        return transactions;
    }

    public List<Transaction> getTransactionsByFromAccount(String accountNumber) {
        return transactionRepository.findTransactionsFromAccount(accountNumber);
    }

    public List<Transaction> getTransactionsByToAccount(String accountNumber) {
        return transactionRepository.findTransactionsToAccount(accountNumber);
    }

    public List<Transaction> getTransactionsByType(String transactionType) {
        return transactionRepository.findByTransactionType(transactionType);
    }

    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByTransactionDateBetween(startDate, endDate);
    }

    public List<Transaction> getTransactionsByBankStatement(Long bankStatementId) {
        return transactionRepository.findByBankStatementId(bankStatementId);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    /**
     * Pobiera wszystkie wpłaty (transakcje przychodzące) pogrupowane po koncie źródłowym
     * @return Mapa z numerem konta -> lista transakcji
     */
    public Map<String, List<Transaction>> getIncomingTransactionsGroupedBySourceAccount() {
        return getAllTransactions().stream()
                .filter(t -> "WPŁATA".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(Transaction::getFromAccountNumber));
    }

    /**
     * Pobiera sumę wpłat dla danego konta źródłowego
     * @param accountNumber Numer konta
     * @return Suma wpłat
     */
    public Double getIncomingTransactionsSumByAccount(String accountNumber) {
        return getTransactionsByFromAccount(accountNumber).stream()
                .filter(t -> "WPŁATA".equals(t.getTransactionType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Pobiera wszystkie wpłaty pogrupowane po koncie źródłowym z sumami
     * OPTIMIZED: Single pass instead of 3 passes, reduces memory allocation
     * @return Mapa: numer konta -> (lista transakcji, suma)
     */
    public Map<String, TransactionGroupData> getIncomingTransactionsGroupedWithSum() {
        return getAllTransactions().stream()
                .filter(t -> "WPŁATA".equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        Transaction::getFromAccountNumber,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                transactions -> {
                                    Double sum = transactions.stream()
                                            .mapToDouble(Transaction::getAmount)
                                            .sum();
                                    return new TransactionGroupData(
                                            transactions.isEmpty() ? "" : transactions.get(0).getFromAccountNumber(),
                                            transactions,
                                            sum
                                    );
                                }
                        )
                ));
    }

    /**
     * Klasa pomocnicza do przechowywania danych o grupie transakcji
     */
    public static class TransactionGroupData {
        public final String accountNumber;
        public final List<Transaction> transactions;
        public final Double totalAmount;

        public TransactionGroupData(String accountNumber, List<Transaction> transactions, Double totalAmount) {
            this.accountNumber = accountNumber;
            this.transactions = transactions;
            this.totalAmount = totalAmount;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }

        public Double getTotalAmount() {
            return totalAmount;
        }

        public Integer getTransactionCount() {
            return transactions.size();
        }
    }
}

