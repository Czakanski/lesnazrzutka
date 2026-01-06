# 🔗 Przewodnik Integracji - Moduł Wpłat

## 📌 Spis Treści
1. [Integracja z Parserem Wyciągów](#integracja-z-parserem-wyciągów)
2. [Integracja z Innymi Modułami](#integracja-z-innymi-modułami)
3. [Migracja Danych](#migracja-danych)
4. [Testowanie Integracji](#testowanie-integracji)

---

## 🔄 Integracja z Parserem Wyciągów

Jeśli masz moduł parsowania wyciągów bankowych, możesz go zintegrować z modułem wpłat:

### Schemat Integracji

```
┌──────────────────────────────────┐
│   BankStatement File Upload      │
│   (PDF/CSV/TXT)                  │
└────────────┬─────────────────────┘
             │
             ▼
┌──────────────────────────────────┐
│   StatementParser.parse()        │
│   (Wyodrębia transakcje)         │
└────────────┬─────────────────────┘
             │
             ▼
┌──────────────────────────────────┐
│   List<Transaction>              │
│   (Parsed Transactions)          │
└────────────┬─────────────────────┘
             │
             ▼
┌──────────────────────────────────┐
│   TransactionService.saveAll()   │
│   (Zapisuje do bazy)             │
└──────────────────────────────────┘
```

### Przykład Kodu

```java
// W kontrolerze obsługującym upload
@PostMapping("/upload")
public String uploadStatement(@RequestParam("file") MultipartFile file) {
    try {
        // 1. Przeczytaj plik
        byte[] fileContent = file.getBytes();
        
        // 2. Sparsuj wyciąg
        List<Transaction> transactions = statementParser.parse(fileContent);
        
        // 3. Utwórz BankStatement
        BankStatement statement = new BankStatement();
        statement.setFileName(file.getOriginalFilename());
        statement.setFileContent(fileContent);
        statement.setFileSize(file.getSize());
        statement.setFileType(file.getContentType());
        statement.setUploadedBy(getCurrentUsername());
        BankStatement savedStatement = bankStatementService.saveBankStatement(statement);
        
        // 4. Powiąż transakcje z wyciągiem
        for (Transaction transaction : transactions) {
            transaction.setBankStatement(savedStatement);
            transaction.setTransactionType("WPŁATA"); // lub odpowiedni typ
            transactionService.saveTransaction(transaction);
        }
        
        return "redirect:/transactions";
    } catch (Exception e) {
        return "error";
    }
}
```

### Interfejs Parsera

```java
public interface StatementParser {
    /**
     * Parsuje zawartość wyciągu i zwraca listę transakcji
     * @param fileContent Zawartość pliku
     * @return Lista wyodrębnionych transakcji
     */
    List<Transaction> parse(byte[] fileContent);
}
```

---

## 🔗 Integracja z Innymi Modułami

### Integracja z AccountsView

```java
// W AccountsView można pokazać wpłaty dla konta
@Route("accounts")
public class AccountsView extends VerticalLayout {
    
    private final TransactionService transactionService;
    
    public void showAccountDetails(String accountNumber) {
        // Pobierz wszystkie wpłaty dla tego konta
        List<Transaction> transactions = 
            transactionService.getTransactionsByFromAccount(accountNumber);
        
        // Pobierz sumę
        Double sum = 
            transactionService.getIncomingTransactionsSumByAccount(accountNumber);
        
        // Wyświetl w UI
        addTransactionsPanel(accountNumber, transactions, sum);
    }
}
```

### Integracja z DashboardView (Już Zrobione ✅)

Dashboard ma przycisk do TransactionsView:
```java
Button viewTransactionsButton = new Button("Przeglądaj Wpłaty");
viewTransactionsButton.addClickListener(event ->
    getUI().ifPresent(ui -> ui.navigate("transactions"))
);
```

### Integracja z ReportsView (Przyszłość)

```java
@Route("reports")
public class ReportsView extends VerticalLayout {
    
    private final TransactionService transactionService;
    
    public void generateReport(LocalDateTime startDate, LocalDateTime endDate) {
        // Pobierz transakcje z okresu
        List<Transaction> transactions = 
            transactionService.getTransactionsByDateRange(startDate, endDate);
        
        // Zgrupuj i oblicz
        Map<String, TransactionGroupData> grouped = 
            transactionService.getIncomingTransactionsGroupedWithSum();
        
        // Wyświetl raport
        displayReport(grouped);
    }
}
```

---

## 📊 Migracja Danych

### Importowanie Danych z Zewnętrznego Źródła

```java
@Service
public class TransactionMigrationService {
    
    private final TransactionService transactionService;
    
    /**
     * Importuje transakcje z CSV
     */
    public void importFromCSV(String csvFilePath) throws IOException {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath))) {
            CSVParser csvParser = new CSVFormat()
                .withFirstRecordAsHeader()
                .parse(reader);
            
            for (CSVRecord record : csvParser) {
                Transaction transaction = new Transaction();
                transaction.setFromAccountNumber(record.get("from_account"));
                transaction.setToAccountNumber(record.get("to_account"));
                transaction.setAmount(Double.parseDouble(record.get("amount")));
                transaction.setTransactionDate(
                    LocalDateTime.parse(record.get("date"))
                );
                transaction.setTransactionType(record.get("type"));
                transaction.setDescription(record.get("description"));
                transaction.setReference(record.get("reference"));
                transaction.setCurrency(record.get("currency"));
                
                transactionService.saveTransaction(transaction);
            }
        }
    }
    
    /**
     * Exportuje transakcje do CSV
     */
    public void exportToCSV(String outputFilePath) throws IOException {
        List<Transaction> transactions = transactionService.getAllTransactions();
        
        try (FileWriter out = new FileWriter(outputFilePath)) {
            CSVPrinter printer = new CSVFormat().print(out);
            
            // Nagłówki
            printer.printRecord("ID", "From Account", "To Account", "Amount", 
                "Date", "Type", "Description", "Reference", "Currency");
            
            // Dane
            for (Transaction t : transactions) {
                printer.printRecord(
                    t.getId(),
                    t.getFromAccountNumber(),
                    t.getToAccountNumber(),
                    t.getAmount(),
                    t.getTransactionDate(),
                    t.getTransactionType(),
                    t.getDescription(),
                    t.getReference(),
                    t.getCurrency()
                );
            }
        }
    }
}
```

### Importowanie z JSON

```java
@PostMapping("/api/transactions/bulk-import")
public ResponseEntity<?> bulkImportTransactions(@RequestBody List<Transaction> transactions) {
    try {
        for (Transaction transaction : transactions) {
            transactionService.saveTransaction(transaction);
        }
        return ResponseEntity.ok("Imported " + transactions.size() + " transactions");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Import failed: " + e.getMessage());
    }
}
```

---

## 🧪 Testowanie Integracji

### Test Integracji TransactionService i Repository

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class TransactionIntegrationTest {
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Test
    @DisplayName("Should integrate with database")
    void testDatabaseIntegration() {
        // Utwórz transakcję
        Transaction transaction = new Transaction();
        transaction.setFromAccountNumber("PL11111111111111111111111111");
        transaction.setToAccountNumber("PL12345678901234567890123456");
        transaction.setAmount(1000.00);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType("WPŁATA");
        
        // Zapisz
        Transaction saved = transactionService.saveTransaction(transaction);
        
        // Sprawdź czy zostały zapisane
        assertNotNull(saved.getId());
        
        // Pobierz z bazy
        Transaction retrieved = transactionService.getTransactionById(saved.getId());
        assertNotNull(retrieved);
        assertEquals(1000.00, retrieved.getAmount());
    }
}
```

### End-to-End Test

```java
@SpringBootTest
@AutoConfigureMockMvc
class TransactionEndToEndTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @DisplayName("Should create and retrieve transaction via API")
    void testEndToEnd() throws Exception {
        // 1. Utwórz transakcję
        String json = "{" +
            "\"fromAccountNumber\": \"PL11111111111111111111111111\"," +
            "\"toAccountNumber\": \"PL12345678901234567890123456\"," +
            "\"amount\": 5000.00," +
            "\"transactionDate\": \"2025-01-06T10:00:00\"," +
            "\"transactionType\": \"WPŁATA\"," +
            "\"description\": \"Test\"," +
            "\"reference\": \"TST001\"," +
            "\"currency\": \"PLN\"" +
            "}";
        
        MvcResult result = mockMvc.perform(post("/api/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk())
            .andReturn();
        
        // 2. Pobierz ID z odpowiedzi
        String response = result.getResponse().getContentAsString();
        // ... parse response ...
        
        // 3. Pobierz transakcję
        mockMvc.perform(get("/api/transactions/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.amount").value(5000.00));
    }
}
```

---

## ✅ Checklist Integracji

- [ ] Transaction model zintegrowany z BankStatement
- [ ] TransactionRepository dostępny
- [ ] TransactionService dostępny
- [ ] TransactionsView dostępna
- [ ] API endpoints działają
- [ ] Dashboard ma przycisk do TransactionsView
- [ ] Testowe dane ładują się
- [ ] Unit testy przechodzą
- [ ] Integracja z parserem wyciągów
- [ ] Migracja danych z systemu zewnętrznego
- [ ] Bezpieczeństwo skonfigurowane

---

## 📝 Notatki

1. **Lazy Loading**: Rozważ lazy loading dla dużych zbiorów danych
2. **Pagination**: Dodaj paginację dla UI przy dużych zbiorach
3. **Caching**: Rozważ caching dla sum i grupowań
4. **Asynchronousness**: Rozważ async operacje dla dużych importów
5. **Validation**: Dodaj walidację danych transakcji
6. **Audit**: Rozważ audit trail dla zmian transakcji

---

**Gotowy do integracji! 🚀**

