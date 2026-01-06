package pl.ostropa.lesnazrzutka.service;

import org.springframework.stereotype.Service;
import pl.ostropa.lesnazrzutka.model.BankStatement;
import pl.ostropa.lesnazrzutka.model.Transaction;
import pl.ostropa.lesnazrzutka.repository.BankStatementRepository;
import pl.ostropa.lesnazrzutka.logging.AppLogger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Service
public class BankStatementService {

    private static final AppLogger logger = AppLogger.getLogger(BankStatementService.class);
    private final BankStatementRepository bankStatementRepository;
    private final TransactionService transactionService;

    public BankStatementService(BankStatementRepository bankStatementRepository,
                                TransactionService transactionService) {
        this.bankStatementRepository = bankStatementRepository;
        this.transactionService = transactionService;
    }

    public BankStatement saveBankStatement(BankStatement statement) {
        return bankStatementRepository.save(statement);
    }

    public BankStatement getBankStatementById(Long id) {
        return bankStatementRepository.findById(id).orElse(null);
    }

    public List<BankStatement> getAllBankStatements() {
        return bankStatementRepository.findAll();
    }

    public List<BankStatement> getAllStatements() {
        return bankStatementRepository.findAll();
    }

    public List<BankStatement> getStatementsByUser(String username) {
        return bankStatementRepository.findByUploadedByOrderByUploadedDateDesc(username);
    }

    public List<BankStatement> getUnprocessedStatements() {
        return bankStatementRepository.findByProcessedFalseOrderByUploadedDateDesc();
    }

    public List<BankStatement> getStatementsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return bankStatementRepository.findByUploadedDateBetween(startDate, endDate);
    }

    public void deleteBankStatement(Long id) {
        bankStatementRepository.deleteById(id);
    }

    public BankStatement markAsProcessed(Long id) {
        BankStatement statement = bankStatementRepository.findById(id).orElse(null);
        if (statement != null) {
            statement.setProcessed(true);
            statement.setProcessedDate(LocalDateTime.now());
            return bankStatementRepository.save(statement);
        }
        return null;
    }

    /**
     * Importuje transakcje z wrzuconego wyciągu bankowego
     * @param statementId ID wyciągu
     * @return Lista zaimportowanych transakcji
     */
    public List<Transaction> importTransactionsFromStatement(Long statementId) {
        try {
            BankStatement statement = getBankStatementById(statementId);
            if (statement == null) {
                logger.business().warn("Wyciąg o ID {} nie znaleziony", statementId);
                return List.of();
            }

            // Parsuj transakcje z zawartości wyciągu
            List<Transaction> transactions = BankStatementParser.parseTransactions(statement);

            // Zapisz wszystkie transakcje do bazy
            List<Transaction> savedTransactions = new ArrayList<>();
            for (Transaction transaction : transactions) {
                try {
                    Transaction saved = transactionService.saveTransaction(transaction);
                    savedTransactions.add(saved);
                } catch (Exception e) {
                    logger.error("Błąd przy zapisywaniu transakcji", e);
                }
            }

            logger.business().info("Zaimportowano {} transakcji z wyciągu: {} ({})",
                savedTransactions.size(), statement.getFileName(), statement.getAccountNumber());

            // Oznaczamy wyciąg jako przetworzony
            markAsProcessed(statementId);

            return savedTransactions;
        } catch (Exception e) {
            logger.error("Błąd przy importowaniu transakcji z wyciągu", e);
            return List.of();
        }
    }

    /**
     * Importuje pojedynczą transakcję
     */
    public Transaction importTransaction(Long statementId, Transaction transaction) {
        BankStatement statement = getBankStatementById(statementId);
        if (statement != null) {
            transaction.setBankStatement(statement);
            return transactionService.saveTransaction(transaction);
        }
        return null;
    }

    /**
     * Pobiera unikalne konta z transakcji
     * @return Lista BankStatement z danymi kontami z transakcji
     */
    public List<BankStatement> getAccountsFromTransactions() {
        try {
            // Pobierz wszystkie wyciągi
            List<BankStatement> allStatements = bankStatementRepository.findAll();

            // Pogrupuj po numerze konta i aggreguj dane
            var accountMap = new java.util.HashMap<String, BankStatement>();

            for (BankStatement statement : allStatements) {
                // Jeśli wyciąg ma numer konta, użyj go
                if (statement.getAccountNumber() != null && !statement.getAccountNumber().isEmpty()) {
                    accountMap.putIfAbsent(statement.getAccountNumber(), statement);
                }
            }

            return new ArrayList<>(accountMap.values());
        } catch (Exception e) {
            logger.error("Błąd przy pobieraniu kont z transakcji", e);
            return new ArrayList<>();
        }
    }
}



