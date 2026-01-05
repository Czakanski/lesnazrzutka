package pl.ostropa.lesnazrzutka.controller;

// JUnit imports
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Spring Boot Test imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// Spring Security imports
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

// JUnit Assertions
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("LoginController - Testy kontrolera logowania")
class LoginControllerTest {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Powinno załadować użytkownika admin")
    void testAdminUserLoads() {
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
    void testRegularUserLoads() {
        // Act
        var user = userDetailsService.loadUserByUsername("user");

        // Assert
        assertNotNull(user);
        assertEquals("user", user.getUsername());
        assertTrue(user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Powinno poprawnie kodować hasła")
    void testPasswordEncoding() {
        // Arrange
        String rawPassword = "testPassword123";

        // Act
        String encoded = passwordEncoder.encode(rawPassword);

        // Assert
        assertNotNull(encoded);
        assertNotEquals(rawPassword, encoded);
        assertTrue(passwordEncoder.matches(rawPassword, encoded));
    }

    @Test
    @DisplayName("Powinno odrzucić złe hasło")
    void testWrongPasswordFails() {
        // Arrange
        String password = "admin";
        String encoded = passwordEncoder.encode(password);

        // Act & Assert
        assertFalse(passwordEncoder.matches("wrongPassword", encoded));
    }

    @Test
    @DisplayName("Admin powinien mieć dwie role")
    void testAdminHasBothRoles() {
        // Act
        var admin = userDetailsService.loadUserByUsername("admin");

        // Assert
        assertEquals(2, admin.getAuthorities().size());
        assertTrue(admin.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(admin.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("User powinien mieć tylko rolę USER")
    void testUserHasOnlyUserRole() {
        // Act
        var user = userDetailsService.loadUserByUsername("user");

        // Assert
        assertEquals(1, user.getAuthorities().size());
        assertTrue(user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
}

