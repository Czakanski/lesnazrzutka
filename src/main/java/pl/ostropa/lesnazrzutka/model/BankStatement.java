package pl.ostropa.lesnazrzutka.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bank_statements")
@SuppressWarnings("unused")
public class BankStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String bankName = "Bank";

    @Column(columnDefinition = "LONGBLOB")
    private byte[] fileContent;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private LocalDateTime uploadedDate = LocalDateTime.now();

    @Column
    private String uploadedBy;

    @Column
    private String description;

    @Column
    private boolean processed = false;

    @Column
    private LocalDateTime processedDate;

    @Column
    private Double accountBalance;

    @Column
    private String accountNumber;

    @OneToMany(mappedBy = "bankStatement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;

    // Constructors
    public BankStatement() {
    }

    public BankStatement(String fileName, String bankName, byte[] fileContent, Long fileSize,
                        String fileType, LocalDateTime uploadedDate, String uploadedBy,
                        String description, boolean processed, LocalDateTime processedDate,
                        Double accountBalance, String accountNumber, List<Transaction> transactions) {
        this.fileName = fileName;
        this.bankName = bankName;
        this.fileContent = fileContent;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.uploadedDate = uploadedDate;
        this.uploadedBy = uploadedBy;
        this.description = description;
        this.processed = processed;
        this.processedDate = processedDate;
        this.accountBalance = accountBalance;
        this.accountNumber = accountNumber;
        this.transactions = transactions;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getBankName() {
        return bankName;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public LocalDateTime getUploadedDate() {
        return uploadedDate;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public String getDescription() {
        return description;
    }

    public boolean isProcessed() {
        return processed;
    }

    public LocalDateTime getProcessedDate() {
        return processedDate;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setUploadedDate(LocalDateTime uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public void setProcessedDate(LocalDateTime processedDate) {
        this.processedDate = processedDate;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "BankStatement{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", bankName='" + bankName + '\'' +
                ", fileSize=" + fileSize +
                ", fileType='" + fileType + '\'' +
                ", uploadedDate=" + uploadedDate +
                ", uploadedBy='" + uploadedBy + '\'' +
                ", description='" + description + '\'' +
                ", processed=" + processed +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }
}

