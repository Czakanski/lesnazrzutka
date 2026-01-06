# 🌲 Lesna Rzutka - Zarządzanie Wyciągami Bankowymi

## 📌 O Projekcie

Aplikacja webowa do zarządzania wyciągami bankowymi z możliwością:
- 📊 Przeglądania wyciągów bankowych
- 💰 Listowania i analizy wpłat
- 🏦 Zarządzania kontami bankowymi
- 📈 Generowania raportów

## 🎨 Design

Projekt wykorzystuje **zielony temat leśny** z ASCII drzewami na stronie logowania.

### Paleta Kolorów
- 🌲 **Główny**: #2d7a4a (Ciemny zielony)
- 🌲 **Jasny**: #4caf50 (Jasny zielony)
- 🌲 **Tło**: #e8f5e9 (Jasno-zielone)

## 🚀 Technologia Stack

| Komponent | Technologia |
|-----------|-------------|
| Framework | Spring Boot 4.0 |
| UI | Vaadin 25.0 |
| Database | H2 (In-Memory) |
| Security | Spring Security |
| ORM | JPA/Hibernate |
| Build | Gradle 8.0 |
| Java | 17+ |

## 📁 Struktura Projektu

```
lesnazrzutka/
├── src/
│   ├── main/
│   │   ├── java/pl/ostropa/lesnazrzutka/
│   │   │   ├── model/
│   │   │   │   ├── BankStatement.java
│   │   │   │   └── Transaction.java        [NEW]
│   │   │   ├── repository/
│   │   │   │   ├── BankStatementRepository.java
│   │   │   │   └── TransactionRepository.java  [NEW]
│   │   │   ├── service/
│   │   │   │   ├── BankStatementService.java
│   │   │   │   └── TransactionService.java     [NEW]
│   │   │   ├── views/
│   │   │   │   ├── DashboardView.java         [UPDATED]
│   │   │   │   ├── AccountsView.java
│   │   │   │   ├── AddBankStatementView.java
│   │   │   │   ├── BankStatementUploadView.java
│   │   │   │   └── TransactionsView.java      [NEW]
│   │   │   ├── controller/
│   │   │   │   ├── LoginController.java
│   │   │   │   └── TransactionController.java [NEW]
│   │   │   └── config/
│   │   │       ├── SecurityConfig.java
│   │   │       └── TransactionDataLoader.java [NEW]
│   │   ├── resources/
│   │   │   ├── application.properties
│   │   │   ├── static/styles.css
│   │   │   └── templates/login.html
│   │   └── frontend/
│   │       ├── index.html                     [UPDATED]
│   │       └── styles/theme.css
│   └── test/
│       └── java/.../service/
│           └── TransactionServiceTest.java    [NEW]
├── docs/
│   ├── TRANSACTIONS_FEATURE.md               [NEW]
│   ├── TESTING_TRANSACTIONS.md               [NEW]
│   ├── TRANSACTIONS_README.md                [NEW]
│   ├── IMPLEMENTATION_SUMMARY.md             [NEW]
│   ├── INTEGRATION_GUIDE.md                  [NEW]
│   └── README.md                             [YOU ARE HERE]
└── build.gradle

```

## 🌟 Nowe Cechy (v1.0.0)

### ✨ Moduł Wpłat

Kompletny system do listowania i analizy wpłat:

#### 🎯 Funkcjonalności
- ✅ Listowanie wszystkich wpłat
- ✅ **Grupowanie po koncie źródłowym** 
- ✅ **Obliczanie sumy dla każdej grupy**
- ✅ Podsumowanie statystyk
- ✅ Responsywny interfejs
- ✅ REST API
- ✅ Testowe dane

#### 📊 Widok Wpłat

```
┌─────────────────────────────────────────┐
│  Podsumowanie                           │
├──────────────┬──────────────┬───────────┤
│ 3 Konta      │ 8 Wpłat      │ 27250 zł  │
└──────────────┴──────────────┴───────────┘

┌─────────────────────────────────────────┐
│ 🏦 Konto: PL61106000760000636213110001 │
│ Suma: 5000.00 zł                       │
├─────────────────────────────────────────┤
│ Konto docelowe | Kwota | Data | Opis  │
├─────────────────────────────────────────┤
│ PL12345...     │ 1500  │ ... │ ...   │
│ PL12345...     │ 2500  │ ... │ ...   │
│ PL12345...     │ 1000  │ ... │ ...   │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│ 🏦 Konto: PL72114020040000300201355387 │
│ Suma: 10500.00 zł                      │
├─────────────────────────────────────────┤
│ ... (tabela transakcji) ...            │
└─────────────────────────────────────────┘
```

#### 🔌 REST API

```bash
# Pobierz wszystkie wpłaty pogrupowane
GET /api/transactions/grouped/all

# Pobierz sumę dla konkretnego konta
GET /api/transactions/sum/PL61106000760000636213110001

# Utwórz nową wpłatę
POST /api/transactions

# Usuń wpłatę
DELETE /api/transactions/{id}
```

## 🔐 Bezpieczeństwo

- Spring Security z uwierzytelnianiem
- Role-based access control (RBAC)
- CSRF protection
- SQL Injection protection (JPA/Hibernate)
- Default login: `admin` / `admin`

## 📚 Dokumentacja

Kompletna dokumentacja dostępna w katalogu `docs/`:

1. **[IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)** - Podsumowanie implementacji
2. **[TRANSACTIONS_FEATURE.md](TRANSACTIONS_FEATURE.md)** - Szczegóły feature'a wpłat
3. **[TRANSACTIONS_README.md](TRANSACTIONS_README.md)** - Kompletna dokumentacja modułu
4. **[TESTING_TRANSACTIONS.md](TESTING_TRANSACTIONS.md)** - Instrukcja testowania
5. **[INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md)** - Przewodnik integracji

## 🚀 Uruchomienie

### Wymagania
- Java 17+
- Gradle 8.0+
- Git

### Kroki

```bash
# 1. Klonuj repozytorium
git clone <repo-url>
cd lesnazrzutka

# 2. Zbuduj projekt
./gradlew clean build -x test

# 3. Uruchom aplikację
./gradlew bootRun

# 4. Otwórz przeglądarkę
# http://localhost:8080

# 5. Zaloguj się
# Login: admin
# Hasło: admin

# 6. Przejdź do wpłat
# Kliknij "Przeglądaj Wpłaty" lub wejdź na /transactions
```

## 🧪 Testowanie

### Unit Tests

```bash
# Uruchom wszystkie testy
./gradlew test

# Uruchom konkretny test
./gradlew test --tests TransactionServiceTest
```

### REST API Test (curl)

```bash
# Pobierz wszystkie wpłaty
curl -u admin:admin http://localhost:8080/api/transactions

# Pobierz sumę dla konta
curl -u admin:admin "http://localhost:8080/api/transactions/sum/PL61106000760000636213110001"

# Utwórz nową wpłatę
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

## 📊 Testowe Dane

Aplikacja automatycznie ładuje testowe dane:
- **3 konta źródłowe**
- **8 transakcji**
- **Razem: 27250 PLN**

Dostęp poprzez: `/transactions` (po zalogowaniu)

## 🔄 Workflow

```
User Login
    ↓
Dashboard
    ├─ Add Bank Statement (Admin only)
    ├─ View History
    ├─ View Accounts
    └─ View Transactions ← NEW!
         ├─ Summary (3 cards)
         └─ Grouped Accounts (with sums)
```

## 📝 Logowanie

| Pole | Wartość |
|------|---------|
| Login | admin |
| Hasło | admin |

Konfiguracja w: `src/main/resources/application.properties`

## 🎯 Główne Funkcjonalności

| Funkcja | Status | Opis |
|---------|--------|------|
| Dashboard | ✅ | Główny widok aplikacji |
| Login/Logout | ✅ | Autoryzacja |
| Upload Wyciągu | ✅ | Dodawanie wyciągów |
| Przeglądanie Kont | ✅ | View accounts |
| **Listowanie Wpłat** | ✅ NEW | Przeglądanie wpłat |
| **Grupowanie Wpłat** | ✅ NEW | Po koncie źródłowym |
| **Sumy Wpłat** | ✅ NEW | Dla każdej grupy |
| REST API | ✅ | HTTP endpoints |

## 🎨 Interfejs

- **Framework**: Vaadin 25.0
- **Tema**: Zielony (leśny)
- **Responsywność**: Mobile-friendly
- **Komponenty**: Grid, Button, Layout, etc.

## 💾 Baza Danych

- **Typ**: H2 In-Memory
- **Tabele**: 
  - `bank_statements`
  - `transactions` (NEW!)
- **Konfiguracja**: `application.properties`

## 📖 Konwencje Kodu

- **Java**: Java 17+ features
- **Naming**: camelCase
- **Packages**: `pl.ostropa.lesnazrzutka.*`
- **Annotations**: Lombok, Spring, JPA
- **Comments**: Polskie i angielskie

## 🔗 Linki

- **Aplikacja**: http://localhost:8080
- **Wpłaty**: http://localhost:8080/transactions
- **API**: http://localhost:8080/api/transactions
- **H2 Console**: http://localhost:8080/h2-console (admin/admin)

## 📞 Support

W przypadku problemów:

1. Sprawdź `IMPLEMENTATION_SUMMARY.md`
2. Sprawdź `TESTING_TRANSACTIONS.md`
3. Sprawdź testy w `src/test/java`
4. Przeczytaj inline komentarze w kodzie

## 📜 Licencja

MIT License

## 👨‍💻 Autor

GitHub Copilot (AI Assistant)

---

## ✅ Checklist - Gotowe do Produkcji

- [x] Model Transaction
- [x] Repository
- [x] Service
- [x] Widok Vaadin
- [x] REST API
- [x] Testowe dane
- [x] Unit testy
- [x] Integracja Dashboard
- [x] Dokumentacja
- [x] Bezpieczeństwo
- [x] Zielony temat

**Status**: 🟢 **GOTOWE DO UŻYTKU**

**Data**: 06.01.2025 (Wersja 1.0.0)

---

**Dziękuję za używanie Lesna Rzutka! 🌲**

