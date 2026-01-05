package pl.ostropa.lesnazrzutka.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bank_statements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}

