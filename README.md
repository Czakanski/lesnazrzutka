# 🌲 Leśns zrzutka - System Zarządzania Wyciągami Bankowymi

Nowoczesna aplikacja webowa do zarządzania wyciągami bankowymi, zbudowana na Spring Boot i Vaadin.

## 📋 Spis treści

- [Opis aplikacji](#opis-aplikacji)
- [Technologia](#technologia)
- [Wymagania](#wymagania)
- [Instalacja](#instalacja)
- [Uruchomienie](#uruchomienie)
- [Instrukcja obsługi](#instrukcja-obsługi)
- [Domyślne konta](#domyślne-konta)
- [Struktura projektu](#struktura-projektu)
- [Deployment](#deployment)

## 📱 Opis aplikacji

**Les na Rzutka** to system do zarządzania wyciągami bankowymi. Umożliwia:

✅ Bezpieczne logowanie użytkowników
✅ Dodawanie i przeglądanie wyciągów bankowych
✅ Śledzenie sald na kontach
✅ Przechowywanie historii transakcji
✅ Panel administratora do zarządzania systemem

### Główne cechy:

- 🎨 **Nowoczesny interfejs** - Responsywny design dostosowany do urządzeń mobilnych
- 🔐 **Bezpieczeństwo** - Uwierzytelnianie Spring Security z szyfrowaniem haseł BCrypt
- 💾 **Baza danych** - H2 in-memory do testów / PostgreSQL do produkcji
- 🚀 **Wydajność** - Zoptymalizowana aplikacja Spring Boot
- 📊 **Wizualizacja** - Czytelne wykresy i tabele sald

## 🛠️ Technologia

| Komponent | Wersja |
|-----------|--------|
| **Java** | 21+ |
| **Spring Boot** | 4.0.1 |
| **Vaadin** | 25.0.2 |
| **Spring Security** | 6.x |
| **Gradle** | 9.2.1 |
| **Thymeleaf** | 3.x |

**Frontend:**
- Vaadin Flow (React + TypeScript)
- HTML5
- CSS3

**Backend:**
- Spring Data JPA
- Hibernate ORM
- H2 Database (dev) / PostgreSQL (prod)

## 📦 Wymagania

### Lokalnie:
- Java 21+ (polecamy Java 21 lub nowsze)
- Gradle 7.x+ (automatycznie pobierane)
- 2GB RAM minimum
- macOS / Linux / Windows

### Na serwerze (Railway/Cloud):
- Java Runtime Environment
- Port 8080 dostępny
- 512MB RAM minimum

## 🚀 Instalacja

### 1. Klonowanie repozytorium

```bash
git clone https://github.com/Czakanski/lesnazrzutka.git
cd lesnazrzutka
```

### 2. Budowanie projektu

```bash
# MacOS / Linux
./gradlew build

# Windows
gradlew.bat build
```

Lub za pomocą skryptu startowego:

```bash
./startApp.sh
```

## ▶️ Uruchomienie

### Lokalnie (dev mode):

```bash
# Metoda 1: Skrypt startowy (rekomendowany)
./startApp.sh

# Metoda 2: Gradle
./gradlew bootRun

# Metoda 3: Uruchomienie JAR-a
java -jar build/libs/app.jar
```

Aplikacja będzie dostępna na: **http://localhost:8080**

### W Docker:

```bash
docker build -t lesnazrzutka .
docker run -p 8080:8080 lesnazrzutka
```

### Na Railway:

```bash
# Push do GitHuba
git push origin main

# W Railway.app:
# 1. Kliknij "New Project"
# 2. Wybierz "Deploy from GitHub"
# 3. Autoryzuj i wybierz repozytorium
# 4. Railway automatycznie:
#    - Skomiluje projekt
#    - Wdroży aplikację
#    - Udostępni na HTTPS
```

## 📖 Instrukcja obsługi

### 1️⃣ Logowanie

#### Ekran logowania
![Login Page](assets/login-screenshot.png)

Na stronie logowania (`/login`) zobaczysz piękny design z lasem ASCII art.

**Domyślne konta:**
| Rola | Login | Hasło |
|------|-------|-------|
| Admin | `admin` | `admin` |
| User | `user` | `user` |

Wpisz dane logowania i kliknij **"Zaloguj się"**

#### Czy zalogowuję się jako admin czy user?

- **Admin** - Pełny dostęp do systemu, może zarządzać wszystkimi wyciągami
- **User** - Podstawowy dostęp, może przeglądać własne wyciągi

### 2️⃣ Dashboard (Główny ekran)

Po zalogowaniu trafiasz na **Dashboard** z dwoma panelami:

#### Lewy panel - Akcje (⚙️)
```
┌─────────────────────────┐
│      ⚙️ AKCJE           │
│                         │
│ Dodaj Wyciąg Bankowy   │
│ Przeglądaj Historię    │
│ Przeglądaj Konta       │
└─────────────────────────┘
```

**Opcje:**

1. **Dodaj Wyciąg Bankowy** 
   - Otwiera formularz do dodawania nowego wyciągu
   - Wgrywaj pliki .csv, .xlsx lub .pdf
   - System automatycznie wyodrębnia dane

2. **Przeglądaj Historię**
   - Widok wszystkich wrzuconych wyciągów
   - Sortowanie i filtrowanie
   - Możliwość pobrania kopii

3. **Przeglądaj Konta**
   - Lista wszystkich kont bankowych
   - Aktualne salda
   - Historia transakcji na każdym koncie

#### Prawy panel - Informacje o Kontach (💳)
```
┌──────────────────────────┐
│  💳 INFORMACJE O KONTACH │
│                          │
│  Numer konta: XX XXXX... │
│  Saldo: 1,234.56 PLN ✅  │
│                          │
│  Numer konta: XX XXXX... │
│  Saldo: -123.45 PLN ⚠️   │
└──────────────────────────┘
```

Wyświetla:
- 🟢 **Zielony** - Dodatnie saldo
- 🔴 **Czerwony** - Ujemne saldo (debet)
- 💰 **Format** - Formatowanie walutowe PLN

### 3️⃣ Dodawanie wyciągu bankowego

#### Krok 1: Kliknij "Dodaj Wyciąg Bankowy"

Otworzy się formularz z polami:
```
┌─────────────────────────────────┐
│  DODAJ WYCIĄG BANKOWY           │
│                                 │
│  [Wkład plik...]                │
│  [Numer konta: ____________]    │
│  [Data wyciągu: ____________]   │
│  [DODAJ]                        │
└─────────────────────────────────┘
```

#### Krok 2: Wgraj plik

- Kliknij pole "Wkład plik..."
- Wybierz plik z wyciągiem (*.csv, *.xlsx, *.pdf)
- System automatycznie czyta dane

#### Krok 3: Uzupełnij dane

- **Numer konta** - 26-cyfrowy numer konta bankowego
- **Data wyciągu** - Data z którą wyciąg ma być powiązany

#### Krok 4: Kliknij DODAJ

- Dane będą załadowane do bazy danych
- Zobaczysz potwierdzenie
- Wyciąg pojawi się w historii

### 4️⃣ Przeglądanie historii wyciągów

#### Kolumny w tabeli:
| Kolumna | Opis |
|---------|------|
| **Data** | Data wczytania wyciągu |
| **Numer konta** | 26-cyfrowy numer konta |
| **Saldo** | Saldo na koncie po wyciągu |
| **Status** | OK / BŁĄD |
| **Akcje** | Pobierz / Usuń |

#### Filtry i sortowanie:
- Sortuj klikając nagłówek kolumny
- Filtruj po numerze konta
- Filtruj po dacie

### 5️⃣ Przeglądanie kont

Lista wszystkich kont z:
- Numerem konta
- Bieżącym saldem
- Ostatnią datą wyciągu
- Liczba transakcji

Kliknij na konto aby zobaczyć szczegóły transakcji.

### 6️⃣ Wylogowanie

Kliknij przycisk **"Wyloguj się"** (góra-prawo)
- Sesja się zakończy
- Zostaniesz przekierowany na stronę logowania

## 🔑 Domyślne konta

Po pierwszym uruchomieniu są dostępne dwa konta:

### Account: Admin
```
Login:    admin
Hasło:    admin
Rola:     ADMIN, USER
Dostęp:   Pełny dostęp do systemu
```

### Account: User
```
Login:    user
Hasło:    user
Rola:     USER
Dostęp:   Przeglądanie i dodawanie wyciągów
```

**Zmiana haseł** - W pliku `src/main/java/pl/ostropa/lesnazrzutka/config/SecurityConfig.java`:

```java
UserDetails admin = User.builder()
    .username("admin")
    .password(passwordEncoder.encode("TWOJE_NOWE_HASŁO"))  // ← tutaj
    .roles("ADMIN", "USER")
    .build();
```

## 📂 Struktura projektu

```
lesnazrzutka/
├── src/
│   ├── main/
│   │   ├── java/pl/ostropa/lesnazrzutka/
│   │   │   ├── LesnazrzutkaApplication.java      # Main app
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java           # Spring Security
│   │   │   ├── controller/
│   │   │   │   └── LoginController.java          # Login endpoint
│   │   │   ├── model/
│   │   │   │   └── BankStatement.java            # JPA Entity
│   │   │   ├── repository/
│   │   │   │   └── BankStatementRepository.java  # JPA Repository
│   │   │   ├── service/
│   │   │   │   └── BankStatementService.java     # Business logic
│   │   │   └── views/
│   │   │       ├── DashboardView.java            # Main dashboard
│   │   │       ├── AddBankStatementView.java     # Add form
│   │   │       ├── AccountsView.java             # Accounts list
│   │   │       └── BankStatementUploadView.java  # Upload view
│   │   ├── resources/
│   │   │   ├── application.properties            # Config
│   │   │   └── templates/
│   │   │       └── login.html                    # Login page
│   │   └── frontend/                             # Vaadin frontend
│   └── test/
│       └── java/                                 # Unit tests
├── build.gradle                                  # Gradle config
├── gradlew / gradlew.bat                        # Gradle wrapper
├── Procfile                                     # Railway config
├── railway.json                                 # Railway config
├── startApp.sh                                  # Start script
└── README.md                                    # This file
```

## 🌐 Deployment

### Railway (Rekomendowany - FREE TIER)

1. **Push do GitHub:**
```bash
git push origin main
```

2. **Na Railway.app:**
   - Zaloguj się na https://railway.app
   - Kliknij "New Project"
   - Wybierz "Deploy from GitHub"
   - Autoryzuj dostęp do GitHuba
   - Wybierz `lesnazrzutka`

3. **Railway automatycznie:**
   - Skomiluje: `./gradlew clean build -x test`
   - Wdroży: `java -jar build/libs/app.jar`
   - Udostępni na: `https://your-app-name.railway.app`

### Heroku

```bash
heroku login
heroku create your-app-name
git push heroku main
```

### Docker

```bash
docker build -t lesnazrzutka:latest .
docker run -p 8080:8080 lesnazrzutka:latest
```

## 🧪 Testy jednostkowe

Projekt zawiera kompletny zestaw testów jednostkowych i integracyjnych.

### Uruchomienie testów

```bash
# Uruchom wszystkie testy
./gradlew test

# Uruchom testy z raportami
./gradlew test --info

# Uruchom konkretny test
./gradlew test --tests BankStatementServiceTest

# Uruchom testy i pokaż raport
./gradlew test && open build/reports/tests/test/index.html
```

### Testy dostępne:

| Klasa | Testy | Opis |
|-------|-------|------|
| **BankStatementServiceTest** | 9 testów | Serwis zarządzania wyciągami |
| **BankStatementRepositoryTest** | 14 testów | Dostęp do bazy danych |
| **SecurityConfigTest** | 7 testów | Konfiguracja bezpieczeństwa |
| **LoginControllerTest** | 10 testów | Kontroler logowania |
| **BankStatementTest** | 15 testów | Model danych |
| **LesnazrzutkaApplicationIntegrationTests** | 8 testów | Testy integracyjne |

**Razem: 63 testy jednostkowe**

### Pokrycie kodu

```bash
# Wygeneruj raport pokrycia
./gradlew test jacocoTestReport

# Otwórz raport HTML
open build/reports/jacoco/test/html/index.html
```

### Testy obejmują:

✅ Logowanie i autentykację
✅ Dodawanie i edytowanie wyciągów
✅ Pobieranie danych z bazy
✅ Formatowanie sald i dat
✅ Obsługę błędów
✅ Walidację danych
✅ Integrację Spring Security
✅ Operacje JPA/Hibernate

## 🔧 Troubleshooting

### Problem: "Port 8080 już zajęty"
```bash
# MacOS / Linux
lsof -i :8080 | grep -v COMMAND | awk '{print $2}' | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Problem: "Cannot find Java"
```bash
# Sprawdź wersję Java
java -version

# Zainstaluj Java 21
# macOS: brew install openjdk@21
# Linux: sudo apt install openjdk-21-jdk
```

### Problem: "Build fails"
```bash
# Wyczyść cache Gradle
./gradlew clean

# Rebuild
./gradlew build -x test
```

## 📧 Wsparcie

Masz pytania? Skontaktuj się:
- 📬 Email: support@lesnarzutka.pl
- 🐛 Issues: https://github.com/Czakanski/lesnazrzutka/issues
- 💬 Dyskusje: https://github.com/Czakanski/lesnazrzutka/discussions

## 📄 Licencja

Projekt jest licencjonowany na licencji MIT. Szczegóły w pliku `LICENSE`.

## 👤 Autor

**Dawid Czakański**
- GitHub: [@Czakanski](https://github.com/Czakanski)
- Email: dawidczakanski@gmail.com

---

**Ostatnia aktualizacja:** 5 stycznia 2026

**Status:** ✅ Production Ready (Gotowy do produkcji)

Dziękujemy za korzystanie z **Les na Rzutka**! 🌲

