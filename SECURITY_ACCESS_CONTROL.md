# 👮 Kontrola dostępu - Podsumowanie zmian

## 🔐 Zabezpieczenie dostępu do funkcji dodawania wyciągów

Implementacja Role-Based Access Control (RBAC) dla funkcji zarządzania wyciągami bankowymi.

## ✅ Co zostało zmienione

### 1. DashboardView.java
**Ukrywanie przycisku dla zwykłych użytkowników**

```java
// Sprawdzenie roli w konstruktorze
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
boolean isAdmin = auth.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(role -> role.equals("ROLE_ADMIN"));

// Ukrycie przycisku dla użytkowników bez roli ADMIN
addStatementButton.setVisible(isAdmin);
```

**Efekt:**
- Przycisk "Dodaj Wyciąg Bankowy" jest widoczny tylko dla adminów
- Zwykli użytkownicy nie widzą opcji dodawania
- Interfejs automatycznie dostosowuje się do roli użytkownika

### 2. AddBankStatementView.java
**Zabezpieczenie na poziomie widoku**

```java
@Route("add-statement")
@Secured("ROLE_ADMIN")  // ← Tylko admini mogą otworzyć
public class AddBankStatementView extends VerticalLayout {
    // ...
}
```

**Efekt:**
- Bezpośredni dostęp do `/add-statement` zwraca 403 Forbidden dla użytkowników bez ROLE_ADMIN
- Zmiana adresu URL bezpośrednio do widoku zostanie zablokowana

### 3. SecurityConfig.java
**Włączenie metody-level security**

```java
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    // ...
}
```

**Efekt:**
- Aktywacja anotacji `@Secured` na poziomie klasy i metody
- Bezpieczniejsza kontrola dostępu na każdym poziomie aplikacji

### 4. Testy zabezpieczenia

#### LoginControllerTest.java - 3 nowe testy:
```java
@Test
@DisplayName("Zabezpieczenie dostępu do add-statement dla niezalogowanego")
void testAddStatementAccessDeniedForUnauthenticated()

@Test
@WithMockUser(username = "user", roles = "USER")
@DisplayName("Zabezpieczenie dostępu dla zwykłego użytkownika")
void testAddStatementAccessDeniedForUser()

@Test
@WithMockUser(username = "admin", roles = "ADMIN")
@DisplayName("Zezwolenie dostępu dla admina")
void testAddStatementAccessAllowedForAdmin()
```

#### DashboardViewTest.java - Nowy plik testowy:
- Test widoczności przycisku dla admina
- Test ukrywania przycisku dla użytkownika

## 🎯 Scenariusze bezpieczeństwa

### Scenariusz 1: Admin loguje się
```
1. Admin wchodzi na stronę
2. Widzi przycisk "Dodaj Wyciąg Bankowy"
3. Klikając przycisk → widok `/add-statement` się otwarza
4. Admin może dodawać wyciągi
5. Operacja zostaje zapisana w bazie
```

### Scenariusz 2: Zwykły użytkownik loguje się
```
1. User wchodzi na stronę
2. Przycisk "Dodaj Wyciąg Bankowy" jest UKRYTY
3. User widzi tylko: "Przeglądaj Historię" i "Przeglądaj Konta"
4. Jeśli User spróbuje bezpośrednio wejść na /add-statement:
   → Otrzymuje błąd 403 Forbidden
5. User nie może dodawać wyciągów
```

### Scenariusz 3: Atak próbujący ominąć zabezpieczenie
```
1. Niezalogowany użytkownik próbuje dostać się do /add-statement
2. Spring Security przekierowuje go na stronę logowania
3. Po zalogowaniu jako user → 403 Forbidden
4. Atak blokowany na 3 poziomach:
   - UI (przycisk ukryty)
   - Controller (anotacja @Secured)
   - HTTP Security (rola wymagana)
```

## 🔍 Poziomy zabezpieczenia

| Poziom | Zabezpieczenie | Status |
|--------|---|---|
| **UI/Widok** | Przycisk ukryty dla użytkownika | ✅ Active |
| **Kontroler** | Anotacja @Secured("ROLE_ADMIN") | ✅ Active |
| **HTTP** | FormLogin wymagany | ✅ Active |
| **Service** | (Opcjonalnie można dodać) | 📋 Planned |
| **Baza** | (Opcjonalnie można dodać Auditing) | 📋 Planned |

## 📊 Testy dostępu

### Całkowita liczba testów dotyczących dostępu:
- **LoginControllerTest**: 3 nowe testy
- **DashboardViewTest**: 2 nowe testy
- **Razem**: 5 nowych testów do sprawdzenia bezpieczeństwa

### Uruchomienie testów:
```bash
# Wszystkie testy bezpieczeństwa
./gradlew test --tests "*Test"

# Testy dostępu do add-statement
./gradlew test --tests "LoginControllerTest.testAddStatement*"

# Testy widoczności przycisku
./gradlew test --tests "DashboardViewTest"
```

## 🚀 Jak to działa w praktyce

### Dla administratora:
```
Login: admin / admin
↓
Dashboard wyświetla: [Dodaj Wyciąg] [Historię] [Konta]
↓
Klik [Dodaj Wyciąg]
↓
Formularz /add-statement załadowany
↓
Admin może dodawać wyciągi
```

### Dla zwykłego użytkownika:
```
Login: user / user
↓
Dashboard wyświetla: [Historię] [Konta] 
(Brakuje przycisku [Dodaj Wyciąg])
↓
Próba dostępu do /add-statement (np. zmiana URL)
↓
❌ Błąd 403 Forbidden
↓
User wraca na dashboard
```

## 📝 Notatki implementacyjne

1. **@Secured vs @RolesAllowed vs @PreAuthorize**
   - Wybraliśmy `@Secured("ROLE_ADMIN")` - najprostsza dla Spring Security
   - Wymaga `@EnableGlobalMethodSecurity(securedEnabled = true)`

2. **Visibility vs Server-side security**
   - Ukrycie przycisku (visibility) jest UI optimization
   - Rzeczywista ochrona na serwerze (403 Forbidden)
   - Zawsze weryfikuj dostęp na serwerze!

3. **Przyszłe ulepszenia**
   - Dodać audit log dla operacji admina
   - Dodać `@PreAuthorize` na metodach serwisu
   - Implementować Row-level security dla bazy

## ✅ Checklist bezpieczeństwa

- [x] Ukrycie UI dla użytkownika
- [x] Zabezpieczenie kontrolera anotacją
- [x] Włączenie Global Method Security
- [x] Testy dostępu dla admin
- [x] Testy dostępu dla user
- [x] Testy dla niezalogowanego
- [x] Dokumentacja zmian

## 📚 Dodatkowe zasoby

- Spring Security Official Docs: https://spring.io/projects/spring-security
- RBAC Pattern: https://en.wikipedia.org/wiki/Role-based_access_control
- OWASP Access Control: https://owasp.org/www-project-top-ten/2021/

---

**Status**: ✅ KOMPLETNE I PRZETESTOWANE
**Data**: 5 stycznia 2026
**Wersja**: 1.0

