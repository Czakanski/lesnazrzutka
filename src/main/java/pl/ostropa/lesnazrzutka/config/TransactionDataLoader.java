package pl.ostropa.lesnazrzutka.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.ostropa.lesnazrzutka.model.Transaction;
import pl.ostropa.lesnazrzutka.service.TransactionService;

import java.time.LocalDateTime;

/**
 * Ładuje testowe dane do bazy danych przy uruchomieniu aplikacji
 * Można wyłączyć jeśli nie chcemy testowych danych
 */
@Component
@RequiredArgsConstructor
public class TransactionDataLoader implements CommandLineRunner {

    private final TransactionService transactionService;

    @Override
    public void run(String... args) throws Exception {
        // Sprawdź czy już istnieją transakcje
        if (!transactionService.getAllTransactions().isEmpty()) {
            return; // Nie ładuj ponownie
        }

        // Testowe dane - wpłaty z różnych kont
        createTestTransactions();
    }

    private void createTestTransactions() {
        // Konto 1: PL61 1060 0076 0000 6362 1311 0001
        transactionService.saveTransaction(createTransaction(
                "PL61106000760000636213110001",
                "PL12345678901234567890123456",
                1500.00,
                LocalDateTime.now().minusDays(10),
                "WPŁATA",
                "Wpłata wynagrodzenia",
                "SALARY001"
        ));

        transactionService.saveTransaction(createTransaction(
                "PL61106000760000636213110001",
                "PL12345678901234567890123456",
                2500.00,
                LocalDateTime.now().minusDays(5),
                "WPŁATA",
                "Wpłata bonus",
                "BONUS001"
        ));

        transactionService.saveTransaction(createTransaction(
                "PL61106000760000636213110001",
                "PL12345678901234567890123456",
                1000.00,
                LocalDateTime.now().minusDays(2),
                "WPŁATA",
                "Wpłata przychody",
                "INCOME001"
        ));

        // Konto 2: PL72 1140 2004 0000 3002 0135 5387
        transactionService.saveTransaction(createTransaction(
                "PL72114020040000300201355387",
                "PL12345678901234567890123456",
                5000.00,
                LocalDateTime.now().minusDays(8),
                "WPŁATA",
                "Wpłata od klienta A",
                "INV001"
        ));

        transactionService.saveTransaction(createTransaction(
                "PL72114020040000300201355387",
                "PL12345678901234567890123456",
                3500.00,
                LocalDateTime.now().minusDays(4),
                "WPŁATA",
                "Wpłata od klienta B",
                "INV002"
        ));

        transactionService.saveTransaction(createTransaction(
                "PL72114020040000300201355387",
                "PL12345678901234567890123456",
                2000.00,
                LocalDateTime.now().minusDays(1),
                "WPŁATA",
                "Wpłata od klienta C",
                "INV003"
        ));

        // Konto 3: PL91 1090 0014 0000 0000 0000 0215
        transactionService.saveTransaction(createTransaction(
                "PL91109000140000000000000215",
                "PL12345678901234567890123456",
                7500.00,
                LocalDateTime.now().minusDays(12),
                "WPŁATA",
                "Wpłata rozliczenia międzybank.",
                "TRANSFER001"
        ));

        transactionService.saveTransaction(createTransaction(
                "PL91109000140000000000000215",
                "PL12345678901234567890123456",
                4250.00,
                LocalDateTime.now().minusDays(6),
                "WPŁATA",
                "Wpłata rozliczenia międzybank.",
                "TRANSFER002"
        ));
    }

    private Transaction createTransaction(
            String fromAccount,
            String toAccount,
            Double amount,
            LocalDateTime date,
            String type,
            String description,
            String reference) {
        Transaction transaction = new Transaction();
        transaction.setFromAccountNumber(fromAccount);
        transaction.setToAccountNumber(toAccount);
        transaction.setAmount(amount);
        transaction.setTransactionDate(date);
        transaction.setTransactionType(type);
        transaction.setDescription(description);
        transaction.setReference(reference);
        transaction.setCurrency("PLN");
        transaction.setCreatedDate(LocalDateTime.now());
        return transaction;
    }
}

