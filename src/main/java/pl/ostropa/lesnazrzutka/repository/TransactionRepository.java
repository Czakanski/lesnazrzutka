package pl.ostropa.lesnazrzutka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.ostropa.lesnazrzutka.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@SuppressWarnings("unused")
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromAccountNumber(String fromAccountNumber);

    List<Transaction> findByToAccountNumber(String toAccountNumber);

    List<Transaction> findByFromAccountNumberOrderByTransactionDateDesc(String fromAccountNumber);

    List<Transaction> findByToAccountNumberOrderByTransactionDateDesc(String toAccountNumber);

    List<Transaction> findByTransactionType(String transactionType);

    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByBankStatementId(Long bankStatementId);

    @Query("SELECT t FROM Transaction t WHERE t.fromAccountNumber = :accountNumber ORDER BY t.transactionDate DESC")
    List<Transaction> findTransactionsFromAccount(@Param("accountNumber") String accountNumber);

    @Query("SELECT t FROM Transaction t WHERE t.toAccountNumber = :accountNumber ORDER BY t.transactionDate DESC")
    List<Transaction> findTransactionsToAccount(@Param("accountNumber") String accountNumber);
}

