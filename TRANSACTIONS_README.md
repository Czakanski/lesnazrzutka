# 💰 Moduł Wpłat - Kompletna Dokumentacja

## 📋 Spis Treści

1. [Przegląd](#przegląd)
2. [Architektura](#architektura)
3. [Instalacja](#instalacja)
4. [Użycie](#użycie)
5. [API Reference](#api-reference)
6. [Przykłady](#przykłady)
7. [FAQ](#faq)

---

## 🎯 Przegląd

Moduł wpłat (`Transactions Module`) umożliwia:

✅ **Listowanie wszystkich wpłat** - przeglądanie transakcji przychodzących
✅ **Grupowanie po koncie** - automatyczne grupowanie wpłat po koncie źródłowym
✅ **Obliczanie sum** - suma wpłat dla każdego konta
✅ **Podsumowanie** - łączne statystyki
✅ **REST API** - programowy dostęp do danych
✅ **Responsywny UI** - interfejs dostosowany do urządzenia

---

## 🏗️ Architektura

### Model View Controller (MVC)

```
┌─────────────────────────────────────────────────────────┐
│                    VAADIN UI LAYER                      │
│  ┌─────────────────────────────────────────────────────┐│
│  │  TransactionsView.java                              ││
│  │  - Summary Section (3 cards)                        ││
│  │  - Account Groups (Grid + Details)                  ││
│  │  - Colors: Green Theme                              ││
│  └─────────────────────────────────────────────────────┘│
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│                    BUSINESS LOGIC LAYER                 │
│  ┌─────────────────────────────────────────────────────┐│
│  │  TransactionService.java                            ││
│  │  - CRUD operations                                  ││
│  │  - Grouping logic                                   ││
│  │  - Sum calculations                                 ││
│  └─────────────────────────────────────────────────────┘│
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│                    DATA ACCESS LAYER                    │
│  ┌─────────────────────────────────────────────────────┐│
│  │  TransactionRepository.java                         ││
│  │  - Database queries                                 ││
│  │  - Spring Data JPA                                  ││
│  └─────────────────────────────────────────────────────┘│
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│                  DATABASE LAYER (H2)                    │
│  ┌─────────────────────────────────────────────────────┐│
│  │  transactions table                                 ││
│  │  - Stores all transaction data                      ││
│  │  - Auto-created by Hibernate                        ││
│  └─────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                      REST API LAYER                     │
│  ┌─────────────────────────────────────────────────────┐│
│  │  TransactionController.java                         ││
│  │  - /api/transactions/* endpoints                    ││
│  │  - JSON request/response                            ││
│  └─────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────┘
```

### Klasy i Relacje

```
BankStatement (1)
    ▲
    │ @OneToMany
    │
Transaction (*)
├── fromAccountNumber: String
├── toAccountNumber: String
├── amount: Double
├── transactionDate: LocalDateTime
├── transactionType: String (WPŁATA, WYPŁATA, PRZELEW)
├── description: String
├── reference: String
└── currency: String (default: PLN)
```

---

## 📦 Instalacja

### Wymagania
- Java 17+
- Spring Boot 4.0+
- Gradle 8.0+
- H2 Database (wbudowana)

### Kroki

1. **Pobierz kod**
```bash
git clone <repo-url>
cd lesnazrzutka
```

2. **Zbuduj projekt**
```bash
./gradlew clean build -x test
```

3. **Uruchom aplikację**
```bash
./gradlew bootRun
```

4. **Otwórz przeglądarkę**
```
http://localhost:8080
```

5. **Zaloguj się**
- Login: `admin`
- Hasło: `admin`

6. **Przejdź do Wpłat**
- Kliknij przycisk "Przeglądaj Wpłaty" na Dashboard
- Lub wejdź bezpośrednio na: `http://localhost:8080/transactions`

---

## 🚀 Użycie

### Via GUI

#### 1. Widok Podsumowania
Na górze strony zobaczysz 3 karty:
- **🏦 Liczba kont źródłowych** - ile różnych kont wysłało wpłaty
- **💸 Liczba wpłat** - suma wszystkich transakcji
- **💰 Suma wszystkich wpłat** - łączna kwota

#### 2. Grupy Kont
Dla każdego konta źródłowego:
- **Nagłówek**: Numer konta + suma wpłat z tego konta
- **Tabela**: Wszystkie wpłaty z następującymi kolumnami:
  - Konto docelowe
  - Kwota (w PLN)
  - Data transakcji
  - Opis
  - Referencja

#### 3. Filtrowanie i Sortowanie
- **Sortowanie**: Kliknij na nagłówek kolumny
- **Przeszukiwanie**: Wpisz w pole wyszukiwania (jeśli dostępne)

### Via REST API

Używaj `curl`, `Postman`, czy innego HTTP klienta.

---

## 📡 API Reference

### 1. Pobierz Wszystkie Transakcje

```http
GET /api/transactions
```

**Response:**
```json
[
  {
    "id": 1,
    "fromAccountNumber": "PL61106000760000636213110001",
    "toAccountNumber": "PL12345678901234567890123456",
    "amount": 1500.00,
    "transactionDate": "2025-01-06T10:30:00",
    "transactionType": "WPŁATA",
    "description": "Wpłata wynagrodzenia",
    "reference": "SALARY001",
    "currency": "PLN"
  }
]
```

### 2. Pobierz Transakcję po ID

```http
GET /api/transactions/{id}
```

**Przykład:**
```http
GET /api/transactions/1
```

**Response:** (jak wyżej)

### 3. Utwórz Nową Transakcję

```http
POST /api/transactions
Content-Type: application/json

{
  "fromAccountNumber": "PL61106000760000636213110001",
  "toAccountNumber": "PL12345678901234567890123456",
  "amount": 2000.00,
  "transactionDate": "2025-01-06T15:00:00",
  "transactionType": "WPŁATA",
  "description": "Nowa wpłata",
  "reference": "NEW001",
  "currency": "PLN"
}
```

### 4. Aktualizuj Transakcję

```http
PUT /api/transactions/{id}
Content-Type: application/json

{
  "fromAccountNumber": "PL61106000760000636213110001",
  "toAccountNumber": "PL12345678901234567890123456",
  "amount": 2500.00,
  "transactionDate": "2025-01-06T15:00:00",
  "transactionType": "WPŁATA",
  "description": "Zaktualizowana wpłata",
  "reference": "NEW001",
  "currency": "PLN"
}
```

### 5. Usuń Transakcję

```http
DELETE /api/transactions/{id}
```

**Odpowiedź:** 204 No Content

### 6. Pobierz Transakcje z Konkretnego Konta

```http
GET /api/transactions/from/{accountNumber}
```

**Przykład:**
```http
GET /api/transactions/from/PL61106000760000636213110001
```

**Response:** (tablica jak w punkcie 1)

### 7. Pobierz Sumę Wpłat

```http
GET /api/transactions/sum/{accountNumber}
```

**Przykład:**
```http
GET /api/transactions/sum/PL61106000760000636213110001
```

**Response:**
```
5000.00
```

### 8. Pobierz Wszystkie Pogrupowane

```http
GET /api/transactions/grouped/all
```

**Response:**
```json
{
  "PL61106000760000636213110001": {
    "accountNumber": "PL61106000760000636213110001",
    "totalAmount": 5000.00,
    "transactionCount": 2,
    "transactions": [...]
  }
}
```

---

## 💡 Przykłady

### Przykład 1: Pobierz Sumę dla Konta (curl)

```bash
curl -u admin:admin \
  "http://localhost:8080/api/transactions/sum/PL61106000760000636213110001"
```

### Przykład 2: Utwórz Nową Wpłatę (curl)

```bash
curl -X POST http://localhost:8080/api/transactions \
  -u admin:admin \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountNumber": "PL11111111111111111111111111",
    "toAccountNumber": "PL12345678901234567890123456",
    "amount": 3000.00,
    "transactionDate": "2025-01-06T14:00:00",
    "transactionType": "WPŁATA",
    "description": "Test Payment",
    "reference": "TST001",
    "currency": "PLN"
  }'
```

### Przykład 3: Programowy Dostęp (Java)

```java
@Autowired
private TransactionService transactionService;

public void demonstrateFeatures() {
    // Pobierz wszystkie transakcje pogrupowane
    Map<String, TransactionGroupData> grouped = 
        transactionService.getIncomingTransactionsGroupedWithSum();
    
    // Iteruj po grupach
    grouped.forEach((accountNumber, groupData) -> {
        System.out.println("Konto: " + accountNumber);
        System.out.println("Suma: " + groupData.getTotalAmount());
        System.out.println("Liczba transakcji: " + groupData.getTransactionCount());
        
        // Iteruj po transakcjach w grupie
        groupData.getTransactions().forEach(t -> {
            System.out.println("  - " + t.getDescription() + ": " + t.getAmount());
        });
    });
}
```

---

## ❓ FAQ

### P: Czy mogę dodawać wpłaty z GUI?
**O:** Aktualnie nie, ale można przez:
- REST API (POST /api/transactions)
- Bezpośrednio w kodzie Java (TransactionService)
- Import z parsera wyciągów bankowych

### P: Czy dane są trwałe?
**O:** Dane są przechowywane w H2 Database:
- W pamięci RAM (domyślnie - tracone po restarcie)
- Można skonfigurować file-based storage

### P: Czy mogę eksportować dane?
**O:** Aktualnie nie, ale można:
- Pobrać JSON przez API
- Implementować eksport do CSV (w przyszłości)

### P: Czy API jest asynchroniczny?
**O:** Nie, wszystkie requesty są synchroniczne. Można dodać `@Async` w przyszłości.

### P: Czy obsługiwane są wiele walut?
**O:** Tak, pole `currency` obsługuje dowolną walutę (domyślnie PLN).

### P: Czy mogę filtrować wpłaty?
**O:** Via API - tak. Via GUI - nie, ale można to dodać.

### P: Jaki jest limit danych?
**O:** H2 in-memory nie ma ustalonego limitu, ale praktycznie do kilka MB.

### P: Czy mogę usuwać wpłaty?
**O:** Tak, przez API (DELETE) lub kod Java.

---

## 🔐 Bezpieczeństwo

- **Autoryzacja**: Spring Security - login wymagany
- **Rola**: ROLE_USER lub ROLE_ADMIN
- **API**: Chroniony HTTP Basic Auth lub Session

---

## 📚 Pliki Źródłowe

| Plik | Lokalizacja | Opis |
|------|-------------|------|
| Transaction.java | model/ | Encja JPA |
| TransactionRepository.java | repository/ | Spring Data JPA |
| TransactionService.java | service/ | Logika biznesowa |
| TransactionsView.java | views/ | Interfejs Vaadin |
| TransactionController.java | controller/ | REST API |
| TransactionDataLoader.java | config/ | Testowe dane |

---

## 🎨 Kolorystyka

| Element | Kolor | Hex |
|---------|-------|-----|
| Primary | Zielony | #2d7a4a |
| Secondary | Ciemny zielony | #1e5631 |
| Success | Jasny zielony | #4caf50 |
| Light BG | Jasne tło | #e8f5e9 |
| Border | Obramowanie | #a5d6a7 |

---

## 📞 Support

W razie pytań, sprawdź:
1. `TRANSACTIONS_FEATURE.md` - Szczegółowy opis
2. `TESTING_TRANSACTIONS.md` - Instrukcja testowania
3. Inline komentarze w kodzie
4. Unit testy w `src/test/java`

---

**Gotowe do użytku! 🎉**

