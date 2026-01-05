package pl.ostropa.lesnazrzutka.repository;

// JUnit imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Spring Boot Test imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

// Model import
import pl.ostropa.lesnazrzutka.model.BankStatement;

// Java imports
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// JUnit Assertions
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("BankStatementRepository - Testy repozytorium")
class BankStatementRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BankStatementRepository bankStatementRepository;

    private BankStatement bankStatement;

    @BeforeEach
    void setUp() {
        bankStatement = new BankStatement();
        bankStatement.setAccountNumber("26 1050 0099 7611 5372 2918 7243");
        bankStatement.setAccountBalance(new BigDecimal("5000.00"));
        bankStatement.setTransactionDate(LocalDate.of(2026, 1, 5));
        bankStatement.setFileName("wyciag_01_2026.csv");
    }

    @Test
    @DisplayName("Powinno zapisać nowy wyciąg do bazy")
    void testSaveBankStatement() {
        // Act
        BankStatement saved = bankStatementRepository.save(bankStatement);
        entityManager.flush();

        // Assert
        assertNotNull(saved.getId());
        assertEquals("26 1050 0099 7611 5372 2918 7243", saved.getAccountNumber());
    }

    @Test
    @DisplayName("Powinno znaleźć wyciąg po ID")
    void testFindById() {
        // Arrange
        BankStatement saved = bankStatementRepository.save(bankStatement);
        entityManager.flush();

        // Act
        var found = bankStatementRepository.findById(saved.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    @DisplayName("Powinno zwrócić pusty Optional dla nieistniejącego ID")
    void testFindByIdNotFound() {
        // Act
        var found = bankStatementRepository.findById(999L);

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Powinno pobrać wszystkie wyciągi")
    void testFindAll() {
        // Arrange
        BankStatement statement1 = new BankStatement();
        statement1.setAccountNumber("26 1050 0099 7611 5372 2918 7243");
        statement1.setAccountBalance(new BigDecimal("5000.00"));

        BankStatement statement2 = new BankStatement();
        statement2.setAccountNumber("26 1050 0099 7611 5372 2918 7244");
        statement2.setAccountBalance(new BigDecimal("3000.00"));

        bankStatementRepository.save(statement1);
        bankStatementRepository.save(statement2);
        entityManager.flush();

        // Act
        List<BankStatement> statements = bankStatementRepository.findAll();

        // Assert
        assertEquals(2, statements.size());
    }

    @Test
    @DisplayName("Powinno zwrócić pustą listę gdy brak wyciągów")
    void testFindAllEmpty() {
        // Act
        List<BankStatement> statements = bankStatementRepository.findAll();

        // Assert
        assertTrue(statements.isEmpty());
    }

    @Test
    @DisplayName("Powinno zaktualizować istniejący wyciąg")
    void testUpdateBankStatement() {
        // Arrange
        BankStatement saved = bankStatementRepository.save(bankStatement);
        entityManager.flush();

        // Act
        saved.setAccountBalance(new BigDecimal("6000.00"));
        BankStatement updated = bankStatementRepository.save(saved);

        // Assert
        assertEquals(new BigDecimal("6000.00"), updated.getAccountBalance());
    }

    @Test
    @DisplayName("Powinno usunąć wyciąg po ID")
    void testDeleteById() {
        // Arrange
        BankStatement saved = bankStatementRepository.save(bankStatement);
        Long id = saved.getId();
        entityManager.flush();

        // Act
        bankStatementRepository.deleteById(id);
        entityManager.flush();

        // Assert
        var found = bankStatementRepository.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Powinno liczyć wyciągi")
    void testCount() {
        // Arrange
        bankStatementRepository.save(bankStatement);

        BankStatement statement2 = new BankStatement();
        statement2.setAccountNumber("26 1050 0099 7611 5372 2918 7244");
        bankStatementRepository.save(statement2);

        entityManager.flush();

        // Act
        long count = bankStatementRepository.count();

        // Assert
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Powinno sprawdzić czy wyciąg istnieje")
    void testExistsById() {
        // Arrange
        BankStatement saved = bankStatementRepository.save(bankStatement);
        entityManager.flush();

        // Act
        boolean exists = bankStatementRepository.existsById(saved.getId());

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Powinno przechowywać duże salda")
    void testLargeBalance() {
        // Arrange
        bankStatement.setAccountBalance(new BigDecimal("999999999.99"));
        BankStatement saved = bankStatementRepository.save(bankStatement);
        entityManager.flush();

        // Act
        var found = bankStatementRepository.findById(saved.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(new BigDecimal("999999999.99"), found.get().getAccountBalance());
    }

    @Test
    @DisplayName("Powinno przechowywać ujemne salda")
    void testNegativeBalance() {
        // Arrange
        bankStatement.setAccountBalance(new BigDecimal("-1000.50"));
        BankStatement saved = bankStatementRepository.save(bankStatement);
        entityManager.flush();

        // Act
        var found = bankStatementRepository.findById(saved.getId());

        // Assert
        assertTrue(found.isPresent());
        assertTrue(found.get().getAccountBalance().compareTo(BigDecimal.ZERO) < 0);
    }
}

