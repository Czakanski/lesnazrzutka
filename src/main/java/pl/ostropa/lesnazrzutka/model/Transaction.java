package pl.ostropa.lesnazrzutka.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@SuppressWarnings("unused")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fromAccountNumber;

    @Column(nullable = false)
    private String toAccountNumber;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @Column
    private String description;

    @Column
    private String transactionType; // WPŁATA, WYPŁATA, PRZELEW

    @ManyToOne
    @JoinColumn(name = "bank_statement_id")
    private BankStatement bankStatement;

    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column
    private String currency = "PLN";

    @Column
    private String reference;

    // Constructors
    public Transaction() {
    }

    public Transaction(String fromAccountNumber, String toAccountNumber, Double amount,
                      LocalDateTime transactionDate, String description, String transactionType,
                      BankStatement bankStatement, LocalDateTime createdDate, String currency, String reference) {
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.description = description;
        this.transactionType = transactionType;
        this.bankStatement = bankStatement;
        this.createdDate = createdDate;
        this.currency = currency;
        this.reference = reference;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getReference() {
        return reference;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public BankStatement getBankStatement() {
        return bankStatement;
    }

    // Setters
    public Transaction setId(Long id) {
        this.id = id;
        return this;
    }

    public Transaction setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
        return this;
    }

    public Transaction setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
        return this;
    }

    public Transaction setAmount(Double amount) {
        this.amount = amount;
        return this;
    }

    public Transaction setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
        return this;
    }

    public Transaction setDescription(String description) {
        this.description = description;
        return this;
    }

    public Transaction setTransactionType(String transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public Transaction setReference(String reference) {
        this.reference = reference;
        return this;
    }

    public Transaction setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public Transaction setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public Transaction setBankStatement(BankStatement bankStatement) {
        this.bankStatement = bankStatement;
        return this;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", fromAccountNumber='" + fromAccountNumber + '\'' +
                ", toAccountNumber='" + toAccountNumber + '\'' +
                ", amount=" + amount +
                ", transactionDate=" + transactionDate +
                ", description='" + description + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", currency='" + currency + '\'' +
                ", reference='" + reference + '\'' +
                '}';
    }
}

