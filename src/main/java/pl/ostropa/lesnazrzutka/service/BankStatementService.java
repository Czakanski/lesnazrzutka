package pl.ostropa.lesnazrzutka.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ostropa.lesnazrzutka.model.BankStatement;
import pl.ostropa.lesnazrzutka.repository.BankStatementRepository;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
public class BankStatementService {

    private final BankStatementRepository bankStatementRepository;

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
}

