package pl.ostropa.lesnazrzutka.service;

import pl.ostropa.lesnazrzutka.model.BankStatement;
import pl.ostropa.lesnazrzutka.model.Transaction;
import pl.ostropa.lesnazrzutka.logging.AppLogger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Parser dla wyciągów bankowych w formacie CSV/TXT
 * Obsługuje różne formaty wyciągów
 */
public class BankStatementParser {

    private static final AppLogger logger = AppLogger.getLogger(BankStatementParser.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Parsuje zawartość wyciągu bankowego
     * @param statement Wyciąg z zawartością pliku
     * @return Lista transakcji
     */
    public static List<Transaction> parseTransactions(BankStatement statement) {
        List<Transaction> transactions = new ArrayList<>();

        try {
            if (statement.getFileContent() == null || statement.getFileContent().length == 0) {
                logger.warn("Plik wyciągu jest pusty: {}", statement.getFileName());
                return transactions;
            }

            String content = new String(statement.getFileContent(), StandardCharsets.UTF_8);

            // Wybierz parser na podstawie typu pliku
            if (statement.getFileName().endsWith(".csv")) {
                transactions = parseCSV(content, statement);
            } else if (statement.getFileName().endsWith(".txt")) {
                transactions = parseTXT(content, statement);
            } else {
                logger.business().warn("Nieznany format pliku: {}", statement.getFileName());
            }

            logger.business().info("Sparsowano {} transakcji z wyciągu: {}",
                transactions.size(), statement.getFileName());

        } catch (Exception e) {
            logger.error("Błąd przy parsowaniu wyciągu: {}", statement.getFileName(), e);
        }

        return transactions;
    }

    /**
     * Parser dla plików CSV
     * Format: Data,Konto źródłowe,Konto docelowe,Kwota,Opis,Typ,Referencja
     */
    private static List<Transaction> parseCSV(String content, BankStatement statement) {
        List<Transaction> transactions = new ArrayList<>();
        String[] lines = content.split("\n");

        // Przeskocz nagłówek (pierwsza linia)
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            try {
                String[] fields = line.split(",");
                if (fields.length < 5) continue;

                Transaction transaction = new Transaction();
                transaction.setTransactionDate(parseDate(fields[0].trim()));
                transaction.setFromAccountNumber(fields[1].trim());
                transaction.setToAccountNumber(fields[2].trim());
                transaction.setAmount(parseAmount(fields[3].trim()));
                transaction.setDescription(fields[4].trim());
                transaction.setTransactionType(fields.length > 5 ? fields[5].trim() : "TRANSFER");
                transaction.setReference(fields.length > 6 ? fields[6].trim() : "");
                transaction.setCurrency("PLN");
                transaction.setCreatedDate(LocalDateTime.now());
                transaction.setBankStatement(statement);

                transactions.add(transaction);
            } catch (Exception e) {
                logger.warn("Błąd przy parsowaniu linii CSV: {}", line, e);
            }
        }

        return transactions;
    }

    /**
     * Parser dla plików TXT
     * Format: wiersze z transakcjami w strukturalnym formacie
     */
    private static List<Transaction> parseTXT(String content, BankStatement statement) {
        List<Transaction> transactions = new ArrayList<>();
        String[] lines = content.split("\n");

        Transaction currentTransaction = null;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Szukaj linii ze datą (yyyy-MM-dd format)
            if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}.*", line)) {
                if (currentTransaction != null) {
                    transactions.add(currentTransaction);
                }

                currentTransaction = new Transaction();
                currentTransaction.setBankStatement(statement);
                currentTransaction.setCreatedDate(LocalDateTime.now());
                currentTransaction.setCurrency("PLN");

                // Parsuj dane z linii
                parseTransactionLine(line, currentTransaction);
            } else if (currentTransaction != null) {
                // Dodawaj do opisu jeśli linia nie jest datą
                if (currentTransaction.getDescription() != null) {
                    currentTransaction.setDescription(currentTransaction.getDescription() + " " + line);
                } else {
                    currentTransaction.setDescription(line);
                }
            }
        }

        if (currentTransaction != null) {
            transactions.add(currentTransaction);
        }

        return transactions;
    }

    /**
     * Parsuje linię transakcji
     */
    private static void parseTransactionLine(String line, Transaction transaction) {
        try {
            // Format przykładowy: 2024-01-15 PL61106000760000636213110001 -> PL12345678901234567890123456 1500.00 PLN Wpłata wynagrodzenia
            String[] parts = line.split("\\s+");

            if (parts.length >= 4) {
                transaction.setTransactionDate(parseDate(parts[0]));
                transaction.setFromAccountNumber(parts[1]);

                // Szukaj strzałki ->
                int arrowIndex = -1;
                for (int i = 2; i < parts.length; i++) {
                    if ("->".equals(parts[i])) {
                        arrowIndex = i;
                        break;
                    }
                }

                if (arrowIndex > 0 && arrowIndex + 1 < parts.length) {
                    transaction.setToAccountNumber(parts[arrowIndex + 1]);

                    if (arrowIndex + 2 < parts.length) {
                        transaction.setAmount(parseAmount(parts[arrowIndex + 2]));
                    }

                    if (arrowIndex + 3 < parts.length) {
                        // Reszta to opis
                        StringBuilder desc = new StringBuilder();
                        for (int i = arrowIndex + 3; i < parts.length; i++) {
                            if (i > arrowIndex + 3) desc.append(" ");
                            desc.append(parts[i]);
                        }
                        transaction.setDescription(desc.toString());
                    }
                }

                transaction.setTransactionType("TRANSFER");
            }
        } catch (Exception e) {
            logger.warn("Błąd przy parsowaniu linii: {}", line, e);
        }
    }

    /**
     * Parsuje datę w różnych formatach
     */
    private static LocalDateTime parseDate(String dateString) {
        try {
            return LocalDateTime.parse(dateString + " 00:00:00", DATETIME_FORMATTER);
        } catch (Exception e) {
            try {
                return java.time.LocalDate.parse(dateString, DATE_FORMATTER).atStartOfDay();
            } catch (Exception ex) {
                logger.warn("Nie można sparsować daty: {}", dateString);
                return LocalDateTime.now();
            }
        }
    }

    /**
     * Parsuje kwotę (obsługuje różne formaty)
     */
    private static Double parseAmount(String amountString) {
        try {
            // Usuń znaki walut i spacje
            String cleaned = amountString
                .replaceAll("[^0-9,.-]", "")
                .replace(",", ".");
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            logger.warn("Nie można sparsować kwoty: {}", amountString);
            return 0.0;
        }
    }
}

