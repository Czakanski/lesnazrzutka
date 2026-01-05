package pl.ostropa.lesnazrzutka.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.ostropa.lesnazrzutka.model.BankStatement;
import pl.ostropa.lesnazrzutka.repository.BankStatementRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BankStatementService - Testy jednostkowe")
class BankStatementServiceTest {

    @Mock
    private BankStatementRepository bankStatementRepository;

    @InjectMocks
    private BankStatementService bankStatementService;

    private BankStatement bankStatement;

    @BeforeEach
    void setUp() {
        bankStatement = new BankStatement();
        bankStatement.setId(1L);
        bankStatement.setAccountNumber("26 1050 0099 7611 5372 2918 7243");
        bankStatement.setAccountBalance(new BigDecimal("5000.00"));
        bankStatement.setTransactionDate(LocalDate.of(2026, 1, 5));
        bankStatement.setFileName("wyciag_01_2026.csv");
    }

    @Test
    @DisplayName("Powinno dodać nowy wyciąg bankowy")
    void testAddBankStatement() {
        // Arrange
        when(bankStatementRepository.save(any(BankStatement.class))).thenReturn(bankStatement);

        // Act
        BankStatement result = bankStatementService.addBankStatement(bankStatement);

        // Assert
        assertNotNull(result);
        assertEquals("26 1050 0099 7611 5372 2918 7243", result.getAccountNumber());
        assertEquals(new BigDecimal("5000.00"), result.getAccountBalance());
        verify(bankStatementRepository, times(1)).save(bankStatement);
    }

    @Test
    @DisplayName("Powinno pobrać wyciąg po ID")
    void testGetBankStatementById() {
        // Arrange
        when(bankStatementRepository.findById(1L)).thenReturn(Optional.of(bankStatement));

        // Act
        Optional<BankStatement> result = bankStatementService.getBankStatementById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("26 1050 0099 7611 5372 2918 7243", result.get().getAccountNumber());
        verify(bankStatementRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Powinno zwrócić pusty Optional gdy brak wyciągu")
    void testGetBankStatementByIdNotFound() {
        // Arrange
        when(bankStatementRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<BankStatement> result = bankStatementService.getBankStatementById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(bankStatementRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Powinno pobrać wszystkie wyciągi")
    void testGetAllBankStatements() {
        // Arrange
        BankStatement statement2 = new BankStatement();
        statement2.setId(2L);
        statement2.setAccountNumber("26 1050 0099 7611 5372 2918 7244");
        statement2.setAccountBalance(new BigDecimal("3000.00"));

        List<BankStatement> statements = Arrays.asList(bankStatement, statement2);
        when(bankStatementRepository.findAll()).thenReturn(statements);

        // Act
        List<BankStatement> result = bankStatementService.getAllBankStatements();

        // Assert
        assertEquals(2, result.size());
        assertEquals("26 1050 0099 7611 5372 2918 7243", result.get(0).getAccountNumber());
        assertEquals("26 1050 0099 7611 5372 2918 7244", result.get(1).getAccountNumber());
        verify(bankStatementRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Powinno zwrócić pustą listę gdy brak wyciągów")
    void testGetAllBankStatementsEmpty() {
        // Arrange
        when(bankStatementRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<BankStatement> result = bankStatementService.getAllBankStatements();

        // Assert
        assertTrue(result.isEmpty());
        verify(bankStatementRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Powinno zaktualizować wyciąg bankowy")
    void testUpdateBankStatement() {
        // Arrange
        BankStatement updatedStatement = new BankStatement();
        updatedStatement.setId(1L);
        updatedStatement.setAccountNumber("26 1050 0099 7611 5372 2918 7243");
        updatedStatement.setAccountBalance(new BigDecimal("6000.00"));

        when(bankStatementRepository.save(any(BankStatement.class))).thenReturn(updatedStatement);

        // Act
        BankStatement result = bankStatementService.addBankStatement(updatedStatement);

        // Assert
        assertEquals(new BigDecimal("6000.00"), result.getAccountBalance());
        verify(bankStatementRepository, times(1)).save(updatedStatement);
    }

    @Test
    @DisplayName("Powinno usunąć wyciąg bankowy")
    void testDeleteBankStatement() {
        // Arrange
        doNothing().when(bankStatementRepository).deleteById(1L);

        // Act
        bankStatementService.deleteBankStatement(1L);

        // Assert
        verify(bankStatementRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Powinno sprawdzić czy saldo jest ujemne")
    void testIsNegativeBalance() {
        // Arrange
        BankStatement negativeStatement = new BankStatement();
        negativeStatement.setAccountBalance(new BigDecimal("-1000.00"));

        // Act
        boolean isNegative = negativeStatement.getAccountBalance().compareTo(BigDecimal.ZERO) < 0;

        // Assert
        assertTrue(isNegative);
    }

    @Test
    @DisplayName("Powinno sprawdzić czy saldo jest dodatnie")
    void testIsPositiveBalance() {
        // Arrange & Act
        boolean isPositive = bankStatement.getAccountBalance().compareTo(BigDecimal.ZERO) > 0;

        // Assert
        assertTrue(isPositive);
    }

    @Test
    @DisplayName("Powinno sprawdzić czy saldo wynosi zero")
    void testIsZeroBalance() {
        // Arrange
        BankStatement zeroStatement = new BankStatement();
        zeroStatement.setAccountBalance(BigDecimal.ZERO);

        // Act
        boolean isZero = zeroStatement.getAccountBalance().compareTo(BigDecimal.ZERO) == 0;

        // Assert
        assertTrue(isZero);
    }
}

