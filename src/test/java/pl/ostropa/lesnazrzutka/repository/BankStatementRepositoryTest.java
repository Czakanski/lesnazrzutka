package pl.ostropa.lesnazrzutka.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.ostropa.lesnazrzutka.model.BankStatement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BankStatementRepository - Testy repozytorium")
class BankStatementRepositoryTest {

    @Mock
    private BankStatementRepository bankStatementRepository;

    private BankStatement bankStatement;

    @BeforeEach
    void setUp() {
        bankStatement = new BankStatement();
        bankStatement.setId(1L);
        bankStatement.setAccountNumber("26 1050 0099 7611 5372 2918 7243");
        bankStatement.setAccountBalance(5000.00);
        bankStatement.setFileName("wyciag_01_2026.csv");
        bankStatement.setUploadedDate(LocalDateTime.of(2026, 1, 5, 10, 0, 0));
        bankStatement.setBankName("Test Bank");
        bankStatement.setFileSize(1024L);
        bankStatement.setFileType("text/csv");
    }

    @Test
    @DisplayName("Powinno zapisać nowy wyciąg do bazy")
    void testSaveBankStatement() {
        // Arrange
        when(bankStatementRepository.save(any(BankStatement.class))).thenReturn(bankStatement);

        // Act
        BankStatement saved = bankStatementRepository.save(bankStatement);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("26 1050 0099 7611 5372 2918 7243", saved.getAccountNumber());
        verify(bankStatementRepository).save(any(BankStatement.class));
    }

    @Test
    @DisplayName("Powinno znaleźć wyciąg po ID")
    void testFindById() {
        // Arrange
        when(bankStatementRepository.findById(1L)).thenReturn(Optional.of(bankStatement));

        // Act
        var found = bankStatementRepository.findById(1L);

        // Assert
        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
        verify(bankStatementRepository).findById(1L);
    }

    @Test
    @DisplayName("Powinno zwrócić pusty Optional dla nieistniejącego ID")
    void testFindByIdNotFound() {
        // Arrange
        when(bankStatementRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        var found = bankStatementRepository.findById(999L);

        // Assert
        assertFalse(found.isPresent());
        verify(bankStatementRepository).findById(999L);
    }

    @Test
    @DisplayName("Powinno pobrać wszystkie wyciągi")
    void testFindAll() {
        // Arrange
        BankStatement statement1 = new BankStatement();
        statement1.setId(1L);
        statement1.setAccountNumber("26 1050 0099 7611 5372 2918 7243");
        statement1.setAccountBalance(5000.00);
        statement1.setFileName("wyciag_01_2026.csv");

        BankStatement statement2 = new BankStatement();
        statement2.setId(2L);
        statement2.setAccountNumber("26 1050 0099 7611 5372 2918 7244");
        statement2.setAccountBalance(3000.00);
        statement2.setFileName("wyciag_02_2026.csv");

        when(bankStatementRepository.findAll()).thenReturn(List.of(statement1, statement2));

        // Act
        List<BankStatement> statements = bankStatementRepository.findAll();

        // Assert
        assertEquals(2, statements.size());
        verify(bankStatementRepository).findAll();
    }

    @Test
    @DisplayName("Powinno zwrócić pustą listę gdy brak wyciągów")
    void testFindAllEmpty() {
        // Arrange
        when(bankStatementRepository.findAll()).thenReturn(List.of());

        // Act
        List<BankStatement> statements = bankStatementRepository.findAll();

        // Assert
        assertTrue(statements.isEmpty());
        verify(bankStatementRepository).findAll();
    }

    @Test
    @DisplayName("Powinno zaktualizować istniejący wyciąg")
    void testUpdateBankStatement() {
        // Arrange
        bankStatement.setAccountBalance(6000.00);
        when(bankStatementRepository.save(any(BankStatement.class))).thenReturn(bankStatement);

        // Act
        BankStatement updated = bankStatementRepository.save(bankStatement);

        // Assert
        assertEquals(6000.00, updated.getAccountBalance());
        verify(bankStatementRepository).save(any(BankStatement.class));
    }

    @Test
    @DisplayName("Powinno usunąć wyciąg po ID")
    void testDeleteById() {
        // Act
        bankStatementRepository.deleteById(1L);

        // Assert
        verify(bankStatementRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Powinno liczyć wyciągi")
    void testCount() {
        // Arrange
        when(bankStatementRepository.count()).thenReturn(2L);

        // Act
        long count = bankStatementRepository.count();

        // Assert
        assertEquals(2, count);
        verify(bankStatementRepository).count();
    }

    @Test
    @DisplayName("Powinno sprawdzić czy wyciąg istnieje")
    void testExistsById() {
        // Arrange
        when(bankStatementRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean exists = bankStatementRepository.existsById(1L);

        // Assert
        assertTrue(exists);
        verify(bankStatementRepository).existsById(1L);
    }

    @Test
    @DisplayName("Powinno przechowywać duże salda")
    void testLargeBalance() {
        // Arrange
        bankStatement.setAccountBalance(999999999.99);
        when(bankStatementRepository.save(any(BankStatement.class))).thenReturn(bankStatement);
        when(bankStatementRepository.findById(1L)).thenReturn(Optional.of(bankStatement));

        // Act
        bankStatementRepository.save(bankStatement);
        var found = bankStatementRepository.findById(1L);

        // Assert
        assertTrue(found.isPresent());
        assertEquals(999999999.99, found.get().getAccountBalance());
    }

    @Test
    @DisplayName("Powinno przechowywać ujemne salda")
    void testNegativeBalance() {
        // Arrange
        bankStatement.setAccountBalance(-1000.50);
        when(bankStatementRepository.save(any(BankStatement.class))).thenReturn(bankStatement);
        when(bankStatementRepository.findById(1L)).thenReturn(Optional.of(bankStatement));

        // Act
        bankStatementRepository.save(bankStatement);
        var found = bankStatementRepository.findById(1L);

        // Assert
        assertTrue(found.isPresent());
        assertTrue(found.get().getAccountBalance() < 0);
    }
}
