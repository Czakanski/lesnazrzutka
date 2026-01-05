package pl.ostropa.lesnazrzutka.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BankStatement - Testy modelu")
class BankStatementTest {

    private BankStatement bankStatement;

    @BeforeEach
    void setUp() {
        bankStatement = new BankStatement();
    }

    @Test
    @DisplayName("Powinno stworzyć nowy BankStatement")
    void testCreateBankStatement() {
        // Act
        BankStatement statement = new BankStatement();

        // Assert
        assertNotNull(statement);
    }

    @Test
    @DisplayName("Powinno ustawić ID")
    void testSetAndGetId() {
        // Act
        bankStatement.setId(1L);

        // Assert
        assertEquals(1L, bankStatement.getId());
    }

    @Test
    @DisplayName("Powinno ustawić numer konta")
    void testSetAndGetAccountNumber() {
        // Act
        String accountNumber = "26 1050 0099 7611 5372 2918 7243";
        bankStatement.setAccountNumber(accountNumber);

        // Assert
        assertEquals(accountNumber, bankStatement.getAccountNumber());
    }

    @Test
    @DisplayName("Powinno ustawić saldo")
    void testSetAndGetAccountBalance() {
        // Act
        BigDecimal balance = new BigDecimal("5000.00");
        bankStatement.setAccountBalance(balance);

        // Assert
        assertEquals(balance, bankStatement.getAccountBalance());
    }

    @Test
    @DisplayName("Powinno ustawić datę transakcji")
    void testSetAndGetTransactionDate() {
        // Act
        LocalDate date = LocalDate.of(2026, 1, 5);
        bankStatement.setTransactionDate(date);

        // Assert
        assertEquals(date, bankStatement.getTransactionDate());
    }

    @Test
    @DisplayName("Powinno ustawić nazwę pliku")
    void testSetAndGetFileName() {
        // Act
        String fileName = "wyciag_01_2026.csv";
        bankStatement.setFileName(fileName);

        // Assert
        assertEquals(fileName, bankStatement.getFileName());
    }

    @Test
    @DisplayName("Powinno poprawnie sformatować saldo w PLN")
    void testBalanceFormatting() {
        // Arrange
        bankStatement.setAccountBalance(new BigDecimal("1234.56"));

        // Act
        BigDecimal balance = bankStatement.getAccountBalance();

        // Assert
        assertEquals(new BigDecimal("1234.56"), balance);
        assertEquals(2, balance.scale());
    }

    @Test
    @DisplayName("Powinno obsługować ujemne saldo")
    void testNegativeBalance() {
        // Act
        bankStatement.setAccountBalance(new BigDecimal("-500.00"));

        // Assert
        assertTrue(bankStatement.getAccountBalance().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    @DisplayName("Powinno obsługować zero saldo")
    void testZeroBalance() {
        // Act
        bankStatement.setAccountBalance(BigDecimal.ZERO);

        // Assert
        assertEquals(0, bankStatement.getAccountBalance().compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Powinno obsługować duże salda")
    void testLargeBalance() {
        // Act
        bankStatement.setAccountBalance(new BigDecimal("999999999.99"));

        // Assert
        assertTrue(bankStatement.getAccountBalance().compareTo(new BigDecimal("999999999.99")) == 0);
    }

    @Test
    @DisplayName("Powinno obsługować daty z przeszłości")
    void testPastDate() {
        // Act
        LocalDate pastDate = LocalDate.of(2020, 1, 1);
        bankStatement.setTransactionDate(pastDate);

        // Assert
        assertEquals(pastDate, bankStatement.getTransactionDate());
        assertTrue(pastDate.isBefore(LocalDate.now()));
    }

    @Test
    @DisplayName("Powinno obsługować przyszłe daty")
    void testFutureDate() {
        // Act
        LocalDate futureDate = LocalDate.of(2030, 12, 31);
        bankStatement.setTransactionDate(futureDate);

        // Assert
        assertEquals(futureDate, bankStatement.getTransactionDate());
        assertTrue(futureDate.isAfter(LocalDate.now()));
    }

    @Test
    @DisplayName("Powinno obsługować 26-cyfrowy numer konta")
    void testValidAccountNumber() {
        // Act
        String validAccountNumber = "26 1050 0099 7611 5372 2918 7243";
        bankStatement.setAccountNumber(validAccountNumber);

        // Assert
        assertEquals(validAccountNumber, bankStatement.getAccountNumber());
        assertTrue(validAccountNumber.length() >= 26);
    }

    @Test
    @DisplayName("Powinno przyjąć null dla pól opcjonalnych")
    void testNullableFields() {
        // Act
        bankStatement.setFileName(null);
        bankStatement.setTransactionDate(null);

        // Assert
        assertNull(bankStatement.getFileName());
        assertNull(bankStatement.getTransactionDate());
    }

    @Test
    @DisplayName("Powinno porównać dwa BankStatements")
    void testEqualBankStatements() {
        // Arrange
        BankStatement statement1 = new BankStatement();
        statement1.setId(1L);
        statement1.setAccountNumber("26 1050 0099 7611 5372 2918 7243");

        BankStatement statement2 = new BankStatement();
        statement2.setId(1L);
        statement2.setAccountNumber("26 1050 0099 7611 5372 2918 7243");

        // Assert
        assertEquals(statement1.getId(), statement2.getId());
        assertEquals(statement1.getAccountNumber(), statement2.getAccountNumber());
    }
}

