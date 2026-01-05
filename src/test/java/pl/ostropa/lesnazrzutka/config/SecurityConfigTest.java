package pl.ostropa.lesnazrzutka.config;

// JUnit imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Spring Boot Test imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// Spring Security imports
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

// JUnit Assertions
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("SecurityConfig - Testy konfiguracji bezpieczeństwa")
class SecurityConfigTest {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Powinno załadować użytkownika admin")
    void testLoadAdminUser() {
        // Act
        var admin = userDetailsService.loadUserByUsername("admin");

        // Assert
        assertNotNull(admin);
        assertEquals("admin", admin.getUsername());
        assertTrue(admin.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Powinno załadować użytkownika user")
    void testLoadUserUser() {
        // Act
        var user = userDetailsService.loadUserByUsername("user");

        // Assert
        assertNotNull(user);
        assertEquals("user", user.getUsername());
        assertTrue(user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Powinno rzucić wyjątek dla nieistniejącego użytkownika")
    void testLoadUserNotFound() {
        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("nonexistent")
        );
    }

    @Test
    @DisplayName("Powinno poprawnie zakodować hasło")
    void testPasswordEncoding() {
        // Arrange
        String rawPassword = "testPassword123";

        // Act
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Assert
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    @DisplayName("Powinno nie dopasować złego hasła")
    void testPasswordEncodingMismatch() {
        // Arrange
        String correctPassword = "admin";
        String wrongPassword = "wrongPassword";
        String encodedPassword = passwordEncoder.encode(correctPassword);

        // Act & Assert
        assertFalse(passwordEncoder.matches(wrongPassword, encodedPassword));
    }

    @Test
    @DisplayName("Powinno mieć włączone konto admin")
    void testAdminUserEnabled() {
        // Act
        var admin = userDetailsService.loadUserByUsername("admin");

        // Assert
        assertTrue(admin.isEnabled());
        assertTrue(admin.isAccountNonExpired());
        assertTrue(admin.isAccountNonLocked());
        assertTrue(admin.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("Powinno mieć włączone konto user")
    void testUserUserEnabled() {
        // Act
        var user = userDetailsService.loadUserByUsername("user");

        // Assert
        assertTrue(user.isEnabled());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
    }
}

