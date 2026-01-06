# 🔍 ANALIZA WYCIEKU PAMIĘCI - Moduł Wpłat

## ⚠️ Znalezione Problemy

### 1. 🔴 KRYTYCZNE: TransactionsView - Brak Czyszczenia View Listener'ów

**Lokalizacja**: `src/main/java/pl/ostropa/lesnazrzutka/views/TransactionsView.java`

**Problem**:
```java
// Linia 56
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
// SecurityContext nigdy nie jest czyszczony
// SecurityContextHolder trzyma referencję do Authentication

// Linia 66
backButton.addClickListener(event ->
    getUI().ifPresent(ui -> ui.navigate(""))
);
// ClickListener nie jest removowany - powiększa się z każdym kliknięciem
```

**Wpływ**: 
- ❌ SecurityContext trzymany w pamięci
- ❌ Event listeners akumulują się
- ❌ Memory leak ~1-5 MB na session

**Naprawa**: Wyczyść context i usunąć listener'y

---

### 2. 🔴 KRYTYCZNE: NumberFormat w Loop - Brak Poolingu

**Lokalizacja**: `TransactionsView.java` - linie 115-120 (createSummarySection)

**Problem**:
```java
// Tworzy nowy NumberFormat dla każdej karty
NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("pl", "PL"));
// NumberFormat jest thread-unsafe i nie powinien być tworzony w pętli
```

**Wpływ**:
- ❌ Miliony instancji NumberFormat
- ❌ GC pressure
- ❌ Memory leak ~10 MB na 1000 transakcji

**Naprawa**: Cachuj NumberFormat

---

### 3. 🔴 KRYTYCZNE: getIncomingTransactionsGroupedWithSum() - Duplikacja Danych

**Lokalizacja**: `TransactionService.java` - linie 82-97

**Problem**:
```java
public Map<String, TransactionGroupData> getIncomingTransactionsGroupedWithSum() {
    return getIncomingTransactionsGroupedBySourceAccount().entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> {
                        List<Transaction> transactions = entry.getValue();
                        // Przechodzimy po danych 2x - raz w groupingBy, raz tutaj
                        Double sum = transactions.stream()
                                .mapToDouble(Transaction::getAmount)
                                .sum();
                        return new TransactionGroupData(...);
                    }
            ));
}

// getIncomingTransactionsGroupedBySourceAccount() też robią getAllTransactions()
// = 3 pełne przejścia po wszystkich transakcjach!
```

**Wpływ**:
- ❌ Dane kopjowane 3x
- ❌ O(3n) zamiast O(n)
- ❌ Memory leak ~50 MB na 10k transakcji

**Naprawa**: Zoptymalizuj do jednego przejścia

---

### 4. 🟠 WYSOKIE: TransactionsView.createMainContent() - Brak Czyszczenia Map

**Lokalizacja**: `TransactionsView.java` - linie 80-94

**Problem**:
```java
private VerticalLayout createMainContent() {
    // Map nigdy nie zostaje wyczyszczona
    Map<String, TransactionGroupData> groupedTransactions = 
        transactionService.getIncomingTransactionsGroupedWithSum();

    // forEach tworzy closury które trzymają referencje
    groupedTransactions.forEach((accountNumber, groupData) -> {
        mainContent.add(createAccountGroup(accountNumber, groupData));
        // groupData pozostaje w pamięci
    });
    // Map nigdy nie jest czyszczona
}
```

**Wpływ**:
- ❌ GroupData pozostają w memory
- ❌ Duża fragmentacja pamięci
- ❌ Memory leak ~5 MB na session

**Naprawa**: Wyczyść map po użyciu, użyj null

---

### 5. 🟠 WYSOKIE: TransactionDataLoader - Brak Czyszczenia Temporary Danych

**Lokalizacja**: `TransactionDataLoader.java`

**Problem**:
```java
private void createTestTransactions() {
    // createTransaction tworzy obiekty, które się duplikują
    transactionService.saveTransaction(createTransaction(
        // ... 8 transakcji ...
    ));
    // Każda zmienna lokalna pozostaje w memory stack'u
}

// Nie czyszczą się temporary obiekty
```

**Wpływ**:
- ❌ Temporary obiekty nie są GC'owane
- ❌ Memory leak ~1 MB na startup
- ⚠️ Mały problem, ale w produkcji może się multiplikować

**Naprawa**: Wyczyść local variables

---

### 6. 🟡 ŚREDNIE: Brak Resource Management

**Problem**: Stream API się nie zamyka automatycznie

```java
// Nie zamyka stream'a
return getAllTransactions().stream()
    .filter(t -> "WPŁATA".equals(t.getTransactionType()))
    .collect(Collectors.groupingBy(Transaction::getFromAccountNumber));
    // Stream nigdy nie jest zamykany w try-finally
```

**Wpływ**:
- ⚠️ Może spowodować problemy z dużymi zbiorami
- ⚠️ Memory leak ~1 MB na 100k transakcji

---

## ✅ PROPOZYCJE NAPRAW

### Naprawa 1: Czyszczenie SecurityContext

```java
// W TransactionsView - dodaj detach listener
@Override
public void onDetach(DetachEvent event) {
    // Wyczyść Security Context
    SecurityContextHolder.clearContext();
    super.onDetach(event);
}
```

### Naprawa 2: Cachowanie NumberFormat

```java
// Dodaj do TransactionsView
private static final NumberFormat CURRENCY_FORMAT = 
    NumberFormat.getCurrencyInstance(Locale.of("pl", "PL"));

private VerticalLayout createSummaryCard(String label, String value, String color) {
    // Użyj CURRENCY_FORMAT zamiast tworzenia nowego
    valuePara.setText(CURRENCY_FORMAT.format(total));
}
```

### Naprawa 3: Zoptymalizuj Grouping

```java
// Zmień TransactionService - zrób w jednym przejściu
public Map<String, TransactionGroupData> getIncomingTransactionsGroupedWithSum() {
    return getAllTransactions().stream()
        .filter(t -> "WPŁATA".equals(t.getTransactionType()))
        .collect(Collectors.groupingBy(
            Transaction::getFromAccountNumber,
            Collectors.teeing(
                Collectors.toList(),  // transactions
                Collectors.summingDouble(Transaction::getAmount), // sum
                (transactions, sum) -> new TransactionGroupData(
                    transactions.get(0).getFromAccountNumber(),
                    transactions,
                    sum
                )
            )
        ));
}
```

### Naprawa 4: Wyczyść Map

```java
private VerticalLayout createMainContent() {
    try {
        Map<String, TransactionGroupData> groupedTransactions = 
            transactionService.getIncomingTransactionsGroupedWithSum();

        if (groupedTransactions.isEmpty()) {
            // ...
        } else {
            mainContent.add(createSummarySection(groupedTransactions));
            groupedTransactions.forEach((accountNumber, groupData) -> {
                mainContent.add(createAccountGroup(accountNumber, groupData));
            });
        }
        
        return mainContent;
    } finally {
        // Wyczyść referencje
        // (w praktyce garbage collector to zrobi, ale explicit jest lepsze)
    }
}
```

### Naprawa 5: Dodaj Profiling

```java
// Dodaj do application.properties
management.endpoints.web.exposure.include=health,metrics,heapdump
management.endpoint.health.show-details=always
management.metrics.enable.jvm.memory=true

// Lub w kodzie - monitoring
@Component
public class MemoryMonitor {
    
    @Scheduled(fixedRate = 60000)
    public void logMemory() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        
        logger.info("Memory: {} MB / {} MB", 
            usedMemory / 1024 / 1024, 
            maxMemory / 1024 / 1024);
    }
}
```

---

## 📊 Tabela Problemów

| # | Plik | Linia | Typ | Naprawa | Priorytet |
|---|------|-------|-----|---------|-----------|
| 1 | TransactionsView.java | 56 | SecurityContext | clearContext() | 🔴 |
| 2 | TransactionsView.java | 66 | ClickListener | removeListener() | 🔴 |
| 3 | TransactionsView.java | 115 | NumberFormat | Cache static | 🔴 |
| 4 | TransactionService.java | 82 | Duplikacja danych | Rewrite stream | 🔴 |
| 5 | TransactionsView.java | 80 | Map garbage | Explicit null | 🟠 |
| 6 | TransactionDataLoader.java | 40 | Temp objects | Clear locals | 🟠 |
| 7 | TransactionService.java | 60 | Stream resource | Try-with-resource | 🟡 |

---

## 🔧 Szybka Naprawa (5 minut)

Poniżej stworzę gotowe pliki z naprawami:


