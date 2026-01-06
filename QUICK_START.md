# ⚡ QUICK START - Moduł Wpłat

## 🎯 W 5 minut do działającej aplikacji

### 1️⃣ Uruchomienie (2 min)

```bash
cd /Users/dawidczakanski/Documents/lesnazrzutka
./gradlew clean build -x test && ./gradlew bootRun
```

### 2️⃣ Login (1 min)

```
URL: http://localhost:8080
Login: admin
Hasło: admin
```

### 3️⃣ Przejdź do Wpłat (1 min)

```
Dashboard → Kliknij "Przeglądaj Wpłaty" 💰
lub
URL: http://localhost:8080/transactions
```

### 4️⃣ Sprawdź Dane (1 min)

Powinieneś zobaczyć:
- ✅ 3 konta źródłowe
- ✅ 8 wpłat
- ✅ Suma: 27250 PLN
- ✅ Pogrupowane po koncie

---

## 🧪 Testowanie API (curl)

```bash
# Pobierz wszystkie pogrupowane
curl -u admin:admin http://localhost:8080/api/transactions/grouped/all

# Pobierz sumę dla konta
curl -u admin:admin "http://localhost:8080/api/transactions/sum/PL61106000760000636213110001"

# Utwórz nową wpłatę
curl -X POST http://localhost:8080/api/transactions \
  -u admin:admin \
  -H "Content-Type: application/json" \
  -d '{"fromAccountNumber":"PL99999999999999999999999999","toAccountNumber":"PL12345678901234567890123456","amount":10000.00,"transactionDate":"2025-01-06T14:00:00","transactionType":"WPŁATA","description":"Test","reference":"TST001","currency":"PLN"}'
```

---

## 📂 Ważne Pliki

| Plik | Opis |
|------|------|
| `TransactionsView.java` | GUI - Widok wpłat |
| `TransactionService.java` | LOGIKA - Grupowanie i sumy |
| `TransactionController.java` | API - REST endpoints |
| `TransactionDataLoader.java` | DANE - Testowe transakcje |

---

## 💡 Kluczowe Metody

```java
// Pobierz wszystkie wpłaty pogrupowane z sumami
Map<String, TransactionGroupData> grouped = 
    transactionService.getIncomingTransactionsGroupedWithSum();

// Pobierz sumę dla konkretnego konta
Double sum = transactionService.getIncomingTransactionsSumByAccount(
    "PL61106000760000636213110001"
);

// Dodaj nową wpłatę
Transaction t = new Transaction();
t.setFromAccountNumber("PL11111111111111111111111111");
t.setAmount(5000.00);
// ... inne pola ...
transactionService.saveTransaction(t);
```

---

## 🎨 Co Się Wyświetla

```
🟢 SUMMARY (3 karty)
├─ 3 Konta
├─ 8 Wpłat
└─ 27250 zł

🏦 KONTO 1: PL61106000760000636213110001
├─ Suma: 5000.00 zł
└─ Tabela z 3 wpłatami

🏦 KONTO 2: PL72114020040000300201355387
├─ Suma: 10500.00 zł
└─ Tabela z 3 wpłatami

🏦 KONTO 3: PL91109000140000000000000215
├─ Suma: 11750.00 zł
└─ Tabela z 2 wpłatami
```

---

## ❓ Najczęstsze Pytania

**P: Gdzie są dane?**
A: W pamięci H2, zostają po restarcie w bieżącej sesji.

**P: Czy mogę dodać nową wpłatę?**
A: Tak! Przez API (POST) lub kod Java.

**P: Czy API wymaga auth?**
A: Tak, login: admin / hasło: admin

**P: Jak dodać własne dane?**
A: Edytuj `TransactionDataLoader.java` lub użyj API.

---

## 📚 Pełna Dokumentacja

- `IMPLEMENTATION_SUMMARY.md` - Podsumowanie
- `TRANSACTIONS_README.md` - Kompletny przewodnik
- `TESTING_TRANSACTIONS.md` - Testy i API
- `INTEGRATION_GUIDE.md` - Integracja z innymi modułami

---

## 🔗 Przydatne Linki

| Link | Opis |
|------|------|
| http://localhost:8080 | Aplikacja |
| http://localhost:8080/transactions | Wpłaty |
| http://localhost:8080/api/transactions | API JSON |
| http://localhost:8080/h2-console | Baza danych |

---

## ✅ Checklist

- [ ] Aplikacja uruchomiona
- [ ] Zalogowany (admin/admin)
- [ ] Widok wpłat dostępny
- [ ] Dane wyświetlane (3 konta, 8 wpłat)
- [ ] API testowany (curl)
- [ ] Wszystko działa! ✨

---

**Gotowe! 🎉 Teraz możesz zacząć używać modułu wpłat.**

Feedback? Problemy? Sprawdź dokumentację powyżej!

