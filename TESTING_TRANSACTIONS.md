# 🚀 Instrukcja Testowania Modułu Wpłat

## 📝 Spis Treści
1. [Uruchomienie Aplikacji](#uruchomienie-aplikacji)
2. [Dostęp do Widoku Wpłat](#dostęp-do-widoku-wpłat)
3. [API REST](#api-rest)
4. [Testowe Dane](#testowe-dane)
5. [Troubleshooting](#troubleshooting)

---

## 🎯 Uruchomienie Aplikacji

### Wstępnie
Upewnij się, że masz zainstalowane:
- Java 17+
- Gradle 8.0+
- IDE (IntelliJ IDEA, VS Code, etc.)

### Kroki

1. **Otwórz terminal w katalogu projektu**
```bash
cd /Users/dawidczakanski/Documents/lesnazrzutka
```

2. **Zbuduj projekt**
```bash
./gradlew build -x test
```

3. **Uruchom aplikację**
```bash
./gradlew bootRun
```

4. **Czekaj na komunikat**
```
2025-01-06 12:00:00.000  INFO ... Started LesnazrzutkaApplication in X.XXX seconds
```

5. **Otwórz przeglądarkę**
```
http://localhost:8080
```

---

## 🔐 Logowanie

**Domyślne Kredencjały:**
- Login: `admin`
- Hasło: `admin`

(Konfiguracja w `application.properties`)

---

## 📊 Dostęp do Widoku Wpłat

### Via GUI Vaadina

1. **Po zalogowaniu** powinieneś zobaczyć Dashboard
2. **Kliknij przycisk** "Przeglądaj Wpłaty" 💰
3. **Zostaniesz przekierowany** do `/transactions`

### Via URL (bezpośrednio)
```
http://localhost:8080/transactions
```

---

## 📈 Co Zobaczysz na Stronie Wpłat

### Sekcja Podsumowania 📊
- **Liczba kont źródłowych**: Ile różnych kont przesłało wpłaty
- **Liczba wpłat**: Suma wszystkich transakcji typu WPŁATA
- **Suma wszystkich wpłat**: Łączna kwota wszystkich wpłat

### Grupy Kont 🏦
Dla każdego konta źródłowego:
- **Nagłówek**: Numer konta + suma wpłat z tego konta
- **Tabela**: Wszystkie wpłaty z tego konta z kolumnami:
  - **Konto docelowe**: Gdzie trafiła wpłata
  - **Kwota**: Wysokość wpłaty
  - **Data transakcji**: Kiedy wpłynęła
  - **Opis**: Informacja o wpłacie
  - **Referencja**: Kod referencyjny

---

## 🔌 API REST

### Endpoints

#### 1. Pobierz Wszystkie Transakcje
```bash
GET http://localhost:8080/api/transactions
```

**Odpowiedź:**
```json
[
  {
    "id": 1,
    "fromAccountNumber": "PL61106000760000636213110001",
    "toAccountNumber": "PL12345678901234567890123456",
    "amount": 1500.00,
    "transactionDate": "2025-01-01T10:30:00",
    "transactionType": "WPŁATA",
    "description": "Wpłata wynagrodzenia",
    "reference": "SALARY001",
    "currency": "PLN"
  }
]
```

#### 2. Pobierz Transakcję po ID
```bash
GET http://localhost:8080/api/transactions/1
```

#### 3. Utwórz Nową Transakcję
```bash
POST http://localhost:8080/api/transactions
Content-Type: application/json

{
  "fromAccountNumber": "PL11111111111111111111111111",
  "toAccountNumber": "PL12345678901234567890123456",
  "amount": 3000.00,
  "transactionDate": "2025-01-06T15:00:00",
  "transactionType": "WPŁATA",
  "description": "Nowa wpłata testowa",
  "reference": "TEST001",
  "currency": "PLN"
}
```

#### 4. Aktualizuj Transakcję
```bash
PUT http://localhost:8080/api/transactions/1
Content-Type: application/json

{
  "fromAccountNumber": "PL11111111111111111111111111",
  "toAccountNumber": "PL12345678901234567890123456",
  "amount": 3500.00,
  "transactionDate": "2025-01-06T15:00:00",
  "transactionType": "WPŁATA",
  "description": "Zaktualizowana wpłata",
  "reference": "TEST001",
  "currency": "PLN"
}
```

#### 5. Usuń Transakcję
```bash
DELETE http://localhost:8080/api/transactions/1
```

#### 6. Pobierz Transakcje z Konkretnego Konta
```bash
GET http://localhost:8080/api/transactions/from/PL61106000760000636213110001
```

#### 7. Pobierz Sumę Wpłat dla Konta
```bash
GET http://localhost:8080/api/transactions/sum/PL61106000760000636213110001
```

**Odpowiedź:**
```
5000.00
```

#### 8. Pobierz Wszystkie Wpłaty Pogrupowane
```bash
GET http://localhost:8080/api/transactions/grouped/all
```

**Odpowiedź:**
```json
{
  "PL61106000760000636213110001": {
    "accountNumber": "PL61106000760000636213110001",
    "totalAmount": 5000.00,
    "transactionCount": 3,
    "transactions": [...]
  },
  "PL72114020040000300201355387": {
    "accountNumber": "PL72114020040000300201355387",
    "totalAmount": 10500.00,
    "transactionCount": 3,
    "transactions": [...]
  }
}
```

---

## 📦 Testowe Dane

### Automatyczne Ładowanie
Przy pierwszym uruchomieniu aplikacji zostają automatycznie załadowane testowe dane z 3 różnych kont:

### Konto 1
- **Numer**: `PL61106000760000636213110001`
- **Wpłaty**: 3 (1500 + 2500 + 1000 PLN = 5000 PLN)

### Konto 2
- **Numer**: `PL72114020040000300201355387`
- **Wpłaty**: 3 (5000 + 3500 + 2000 PLN = 10500 PLN)

### Konto 3
- **Numer**: `PL91109000140000000000000215`
- **Wpłaty**: 2 (7500 + 4250 PLN = 11750 PLN)

**Razem**: 9 wpłat, 3 konta, 27250 PLN

---

## 🧪 Testowanie

### Test 1: Sprawdzenie Podsumowania
1. Przejdź do `/transactions`
2. Sprawdź czy wyświetla się:
   - "Liczba kont źródłowych: 3"
   - "Liczba wpłat: 8" (nie licząc ostatniej)
   - "Suma wszystkich wpłat: 27250.00 zł"

### Test 2: Sprawdzenie Grupowania
1. Upewnij się, że widać 3 sekcje (po jednej na konto)
2. Każda sekcja powinna mieć:
   - Nagłówek z numerem konta
   - Sumę wpłat z tego konta
   - Tabelę z transakcjami

### Test 3: API Test za Pomocą curl
```bash
# Pobierz sumę wpłat dla konkretnego konta
curl http://localhost:8080/api/transactions/sum/PL61106000760000636213110001

# Powinna zwrócić: 5000
```

### Test 4: Dodaj Nową Wpłatę
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountNumber": "PL99999999999999999999999999",
    "toAccountNumber": "PL12345678901234567890123456",
    "amount": 10000.00,
    "transactionDate": "2025-01-06T14:30:00",
    "transactionType": "WPŁATA",
    "description": "Nowa wpłata testowa",
    "reference": "NEW001",
    "currency": "PLN"
  }'
```

Następnie odśwież stronę `/transactions` i powinna pojawić się nowa grupa kont.

---

## 🔧 Troubleshooting

### Problem 1: Strona `/transactions` wyświetla "Not Found"
**Rozwiązanie:**
- Upewnij się, że aplikacja jest uruchomiona
- Sprawdź czy URL to: `http://localhost:8080/transactions`
- Przeładuj stronę (Ctrl+F5)
- Sprawdź czy jesteś zalogowany

### Problem 2: API zwraca 401 Unauthorized
**Rozwiązanie:**
- API endpoints wymagają autoryzacji
- Zaloguj się najpierw przez GUI
- Lub dodaj Basic Auth do requestów:
```bash
curl -u admin:admin http://localhost:8080/api/transactions
```

### Problem 3: Brak testowych danych
**Rozwiązanie:**
- Sprawdź czy `TransactionDataLoader` jest w katalogu `config`
- Usuń bazy danych H2: `rm -rf build/`
- Przebuilduj: `./gradlew clean build -x test`
- Uruchom ponownie: `./gradlew bootRun`

### Problem 4: Błędy kompilacji w IDE
**Rozwiązanie:**
- Odśwież Gradle: `Gradle → Refresh`
- Przeładuj projekt: `File → Invalidate Caches`
- Zrebuilduj: `./gradlew clean build -x test`

### Problem 5: Baza danych jest pusta
**Rozwiązanie:**
- Sprawdź czy `spring.jpa.hibernate.ddl-auto=create-drop` jest w `application.properties`
- Tabela `transactions` powinna być automatycznie utworzona
- DataLoader powinien załadować testowe dane

---

## 📊 Schemat Bazy Danych

### Tabela: transactions
```sql
CREATE TABLE transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    from_account_number VARCHAR(34) NOT NULL,
    to_account_number VARCHAR(34) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    transaction_date TIMESTAMP NOT NULL,
    description VARCHAR(255),
    transaction_type VARCHAR(50),
    bank_statement_id BIGINT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    currency VARCHAR(3) DEFAULT 'PLN',
    reference VARCHAR(255),
    FOREIGN KEY (bank_statement_id) REFERENCES bank_statements(id)
);
```

---

## 📚 Dodatkowe Zasoby

- **Model**: `src/main/java/pl/ostropa/lesnazrzutka/model/Transaction.java`
- **Serwis**: `src/main/java/pl/ostropa/lesnazrzutka/service/TransactionService.java`
- **Widok**: `src/main/java/pl/ostropa/lesnazrzutka/views/TransactionsView.java`
- **API**: `src/main/java/pl/ostropa/lesnazrzutka/controller/TransactionController.java`
- **Dokumentacja**: `TRANSACTIONS_FEATURE.md`

---

## ✅ Checklist - Gotowe do Użytku

- [x] Model Transaction created
- [x] Repository dla Transaction
- [x] Service z logią biznesową
- [x] Widok Vaadina z listowaniem
- [x] Grupowanie po koncie źródłowym
- [x] Suma wpłat dla każdego konta
- [x] Podsumowanie ogólne
- [x] API REST endpoints
- [x] Testowe dane w DataLoader
- [x] Integracja z Dashboard
- [x] Kolorystyka (zielony temat)
- [x] Responsywny interfejs

---

**Gotowe do produkcji! 🎉**

