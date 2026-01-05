package pl.ostropa.lesnazrzutka.test;

import pl.ostropa.lesnazrzutka.model.BankStatement;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Utility class do tworzenia testowych danych dla BankStatement
 */
public class BankStatementTestBuilder {

    private Long id;
    private String accountNumber = "26 1050 0099 7611 5372 2918 7243";
    private BigDecimal accountBalance = new BigDecimal("5000.00");
    private LocalDate transactionDate = LocalDate.now();
    private String fileName = "test_wyciag.csv";

    public BankStatementTestBuilder() {
    }

    public BankStatementTestBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public BankStatementTestBuilder withAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public BankStatementTestBuilder withAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
        return this;
    }

    public BankStatementTestBuilder withTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
        return this;
    }

    public BankStatementTestBuilder withFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public BankStatementTestBuilder withNegativeBalance() {
        this.accountBalance = new BigDecimal("-1000.00");
        return this;
    }

    public BankStatementTestBuilder withLargeBalance() {
        this.accountBalance = new BigDecimal("999999999.99");
        return this;
    }

    public BankStatementTestBuilder withZeroBalance() {
        this.accountBalance = BigDecimal.ZERO;
        return this;
    }

    public BankStatement build() {
        BankStatement statement = new BankStatement();
        statement.setId(id);
        statement.setAccountNumber(accountNumber);
        statement.setAccountBalance(accountBalance);
        statement.setTransactionDate(transactionDate);
        statement.setFileName(fileName);
        return statement;
    }

    public static BankStatementTestBuilder aDefaultBankStatement() {
        return new BankStatementTestBuilder();
    }

    public static BankStatementTestBuilder aBankStatementWithId(Long id) {
        return new BankStatementTestBuilder().withId(id);
    }
}

