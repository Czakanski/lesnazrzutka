package pl.ostropa.lesnazrzutka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ostropa.lesnazrzutka.model.BankStatement;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@SuppressWarnings("unused")
public interface BankStatementRepository extends JpaRepository<BankStatement, Long> {
    List<BankStatement> findByBankName(String bankName);
    List<BankStatement> findByUploadedByOrderByUploadedDateDesc(String uploadedBy);
    List<BankStatement> findByProcessedFalseOrderByUploadedDateDesc();
    List<BankStatement> findByUploadedDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}

