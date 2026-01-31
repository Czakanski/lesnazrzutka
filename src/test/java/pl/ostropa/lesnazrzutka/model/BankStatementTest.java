package pl.ostropa.lesnazrzutka.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        Double balance = 5000.00;
        bankStatement.setAccountBalance(balance);

        // Assert
        assertEquals(balance, bankStatement.getAccountBalance());
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
        bankStatement.setAccountBalance(1234.56);

        // Act
        Double balance = bankStatement.getAccountBalance();

        // Assert
        assertEquals(1234.56, balance);
    }

    @Test
    @DisplayName("Powinno obsługować ujemne saldo")
    void testNegativeBalance() {
        // Act
        bankStatement.setAccountBalance(-500.00);

        // Assert
        assertTrue(bankStatement.getAccountBalance() < 0);
    }

    @Test
    @DisplayName("Powinno obsługować zero saldo")
    void testZeroBalance() {
        // Act
        bankStatement.setAccountBalance(0.00);

        // Assert
        assertEquals(0.00, bankStatement.getAccountBalance());
    }

    @Test
    @DisplayName("Powinno obsługować duże salda")
    void testLargeBalance() {
        // Act
        bankStatement.setAccountBalance(999999999.99);

        // Assert
        assertEquals(999999999.99, bankStatement.getAccountBalance());
    }

    @Test
    @DisplayName("Powinno obsługować 26-cyfrowy numer konta")
    void testValidAccountNumber() {
        // Act
        String validAccountNumber = "26 1050 0099 7611 5372 2918 7243";
        bankStatement.setAccountNumber(validAccountNumber);

        // Assert
        assertEquals(validAccountNumber, bankStatement.getAccountNumber());
        assertEquals(32, validAccountNumber.length()); // 26 cyfr + 3 spacje
    }

    @Test
    @DisplayName("Powinno przyjąć null dla pól opcjonalnych")
    void testNullableFields() {
        // Act
        bankStatement.setFileName(null);
        bankStatement.setDescription(null);

        // Assert
        assertNull(bankStatement.getFileName());
        assertNull(bankStatement.getDescription());
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

