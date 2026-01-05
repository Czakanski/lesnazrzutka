package pl.ostropa.lesnazrzutka;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.ostropa.lesnazrzutka.repository.BankStatementRepository;
import pl.ostropa.lesnazrzutka.service.BankStatementService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("LesnazrzutkaApplication - Testy integracyjne")
class LesnazrzutkaApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private BankStatementRepository bankStatementRepository;

    @Autowired
    private BankStatementService bankStatementService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Powinno załadować kontekst aplikacji")
    void testApplicationContextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    @DisplayName("Powinno załadować BankStatementRepository")
    void testBankStatementRepositoryLoads() {
        assertNotNull(bankStatementRepository);
    }

    @Test
    @DisplayName("Powinno załadować BankStatementService")
    void testBankStatementServiceLoads() {
        assertNotNull(bankStatementService);
    }

    @Test
    @DisplayName("Powinno załadować UserDetailsService")
    void testUserDetailsServiceLoads() {
        assertNotNull(userDetailsService);
    }

    @Test
    @DisplayName("Powinno załadować PasswordEncoder")
    void testPasswordEncoderLoads() {
        assertNotNull(passwordEncoder);
    }

    @Test
    @DisplayName("Powinno mieć wszystkie wymagane beany")
    void testAllBeansArePresent() {
        // Sprawdź czy aplikacja zawiera wymagane beany
        assertTrue(applicationContext.containsBean("bankStatementRepository"));
        assertTrue(applicationContext.containsBean("bankStatementService"));
        assertTrue(applicationContext.containsBean("userDetailsService"));
        assertTrue(applicationContext.containsBean("passwordEncoder"));
    }

    @Test
    @DisplayName("Powinno poprawnie działać ze Spring Security")
    void testSpringSecurityIntegration() {
        // Sprawdź czy można zalogować się na admin
        assertNotNull(userDetailsService.loadUserByUsername("admin"));

        // Sprawdź czy można zalogować się na user
        assertNotNull(userDetailsService.loadUserByUsername("user"));
    }

    @Test
    @DisplayName("Powinno mieć działającą bazę danych")
    void testDatabaseIntegration() {
        // Sprawdź czy można wczytać dane z bazy
        long count = bankStatementRepository.count();
        assertGreaterThanOrEqual(count, 0);
    }

    @Test
    @DisplayName("Powinno poprawnie kodować hasła BCrypt")
    void testPasswordEncodingIntegration() {
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Sprawdź czy hasło jest zakodowane
        assertNotEquals(rawPassword, encodedPassword);

        // Sprawdź czy zakodowane hasło pasuje do original
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    @DisplayName("Aplikacja powinna się uruchomić bez błędów")
    void testApplicationStartsSuccessfully() {
        // Jeśli dotarliśmy tutaj, aplikacja się uruchomiła
        assertNotNull(applicationContext);
        assertNotNull(applicationContext.getApplicationName());
    }
}

