package pl.ostropa.lesnazrzutka.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ostropa.lesnazrzutka.model.Transaction;
import pl.ostropa.lesnazrzutka.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
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
     * @return Mapa: numer konta -> (lista transakcji, suma)
     */
    public Map<String, TransactionGroupData> getIncomingTransactionsGroupedWithSum() {
        return getIncomingTransactionsGroupedBySourceAccount().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            List<Transaction> transactions = entry.getValue();
                            Double sum = transactions.stream()
                                    .mapToDouble(Transaction::getAmount)
                                    .sum();
                            return new TransactionGroupData(entry.getKey(), transactions, sum);
                        }
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

