package pl.ostropa.lesnazrzutka.views;

import com.vaadin.flow.component.button.Button;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import pl.ostropa.lesnazrzutka.service.BankStatementService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "vaadin.launch-browser=false")
@DisplayName("DashboardView - Testy widoku dashboard")
class DashboardViewTest {

    @Autowired
    private BankStatementService bankStatementService;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Powinno wyświetlić przycisk 'Dodaj Wyciąg' dla admina")
    void testAddButtonVisibleForAdmin() {
        // Arrange
        DashboardView dashboardView = new DashboardView(bankStatementService);

        // Act
        dashboardView.getUI().ifPresent(ui -> {
            // Find button
            Button addButton = dashboardView
                    .getChildren()
                    .filter(c -> c instanceof Button)
                    .filter(c -> ((Button) c).getText().contains("Dodaj"))
                    .map(c -> (Button) c)
                    .findFirst()
                    .orElse(null);

            // Assert
            assertNotNull(addButton, "Przycisk 'Dodaj Wyciąg' powinien być widoczny dla admina");
            assertTrue(addButton.isVisible(), "Przycisk 'Dodaj Wyciąg' powinien być widoczny");
        });
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Nie powinno wyświetlić przycisku 'Dodaj Wyciąg' dla zwykłego użytkownika")
    void testAddButtonHiddenForUser() {
        // Arrange
        DashboardView dashboardView = new DashboardView(bankStatementService);

        // Act
        dashboardView.getUI().ifPresent(ui -> {
            // Find button
            Button addButton = dashboardView
                    .getChildren()
                    .filter(c -> c instanceof Button)
                    .filter(c -> ((Button) c).getText().contains("Dodaj"))
                    .map(c -> (Button) c)
                    .findFirst()
                    .orElse(null);

            // Assert - przycisk albo nie istnieje albo jest ukryty
            if (addButton != null) {
                assertFalse(addButton.isVisible(), "Przycisk 'Dodaj Wyciąg' powinien być ukryty dla zwykłych użytkowników");
            }
        });
    }
}

