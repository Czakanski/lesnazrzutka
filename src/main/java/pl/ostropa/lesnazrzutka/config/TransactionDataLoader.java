package pl.ostropa.lesnazrzutka.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.ostropa.lesnazrzutka.model.Transaction;
import pl.ostropa.lesnazrzutka.service.TransactionService;

import java.time.LocalDateTime;

/**
 * Ładuje testowe dane do bazy danych przy uruchomieniu aplikacji
 */
@Component
public class TransactionDataLoader implements CommandLineRunner {

    private final TransactionService transactionService;

    public TransactionDataLoader(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!transactionService.getAllTransactions().isEmpty()) {
            return;
        }

        try {
            createTestTransactions();
        } finally {
            // GC will clean up
        }
    }

    private void createTestTransactions() {
        // Konto 1: PL61 1060 0076 0000 6362 1311 0001
        transactionService.saveTransaction(new Transaction()
                .setFromAccountNumber("PL61106000760000636213110001")
                .setToAccountNumber("PL12345678901234567890123456")
                .setAmount(1500.00)
                .setTransactionDate(LocalDateTime.now().minusDays(10))
                .setTransactionType("WPŁATA")
                .setDescription("Wpłata wynagrodzenia")
                .setReference("SALARY001")
                .setCurrency("PLN")
                .setCreatedDate(LocalDateTime.now())
        );

        // ... remaining transactions ...
        transactionService.saveTransaction(new Transaction()
                .setFromAccountNumber("PL61106000760000636213110001")
                .setToAccountNumber("PL12345678901234567890123456")
                .setAmount(2500.00)
                .setTransactionDate(LocalDateTime.now().minusDays(5))
                .setTransactionType("WPŁATA")
                .setDescription("Wpłata bonus")
                .setReference("BONUS001")
                .setCurrency("PLN")
                .setCreatedDate(LocalDateTime.now())
        );

        transactionService.saveTransaction(new Transaction()
                .setFromAccountNumber("PL61106000760000636213110001")
                .setToAccountNumber("PL12345678901234567890123456")
                .setAmount(1000.00)
                .setTransactionDate(LocalDateTime.now().minusDays(2))
                .setTransactionType("WPŁATA")
                .setDescription("Wpłata przychody")
                .setReference("INCOME001")
                .setCurrency("PLN")
                .setCreatedDate(LocalDateTime.now())
        );

        // Konto 2
        transactionService.saveTransaction(new Transaction()
                .setFromAccountNumber("PL72114020040000300201355387")
                .setToAccountNumber("PL12345678901234567890123456")
                .setAmount(5000.00)
                .setTransactionDate(LocalDateTime.now().minusDays(8))
                .setTransactionType("WPŁATA")
                .setDescription("Wpłata od klienta A")
                .setReference("INV001")
                .setCurrency("PLN")
                .setCreatedDate(LocalDateTime.now())
        );

        transactionService.saveTransaction(new Transaction()
                .setFromAccountNumber("PL72114020040000300201355387")
                .setToAccountNumber("PL12345678901234567890123456")
                .setAmount(3500.00)
                .setTransactionDate(LocalDateTime.now().minusDays(4))
                .setTransactionType("WPŁATA")
                .setDescription("Wpłata od klienta B")
                .setReference("INV002")
                .setCurrency("PLN")
                .setCreatedDate(LocalDateTime.now())
        );

        transactionService.saveTransaction(new Transaction()
                .setFromAccountNumber("PL72114020040000300201355387")
                .setToAccountNumber("PL12345678901234567890123456")
                .setAmount(2000.00)
                .setTransactionDate(LocalDateTime.now().minusDays(1))
                .setTransactionType("WPŁATA")
                .setDescription("Wpłata od klienta C")
                .setReference("INV003")
                .setCurrency("PLN")
                .setCreatedDate(LocalDateTime.now())
        );

        // Konto 3
        transactionService.saveTransaction(new Transaction()
                .setFromAccountNumber("PL91109000140000000000000215")
                .setToAccountNumber("PL12345678901234567890123456")
                .setAmount(7500.00)
                .setTransactionDate(LocalDateTime.now().minusDays(12))
                .setTransactionType("WPŁATA")
                .setDescription("Wpłata rozliczenia międzybank.")
                .setReference("TRANSFER001")
                .setCurrency("PLN")
                .setCreatedDate(LocalDateTime.now())
        );

        transactionService.saveTransaction(new Transaction()
                .setFromAccountNumber("PL91109000140000000000000215")
                .setToAccountNumber("PL12345678901234567890123456")
                .setAmount(4250.00)
                .setTransactionDate(LocalDateTime.now().minusDays(6))
                .setTransactionType("WPŁATA")
                .setDescription("Wpłata rozliczenia międzybank.")
                .setReference("TRANSFER002")
                .setCurrency("PLN")
                .setCreatedDate(LocalDateTime.now())
        );
    }
}

