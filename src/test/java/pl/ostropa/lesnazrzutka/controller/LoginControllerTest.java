package pl.ostropa.lesnazrzutka.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("LoginController - Testy kontrolera logowania")
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Powinno zwrócić stronę logowania")
    void testLoginPageGet() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Lesna zrzutka")));
    }

    @Test
    @DisplayName("Powinno przekierować niezalogowanego na /login")
    void testUnauthenticatedRedirectToLogin() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Powinno zezwolić dostęp do /login bez autentykacji")
    void testLoginPageIsAccessibleWithoutAuth() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Powinno odrzucić login z pustym użytkownikiem")
    void testLoginWithEmptyUsername() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/login")
                .param("username", "")
                .param("password", "admin"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Powinno odrzucić login z pustym hasłem")
    void testLoginWithEmptyPassword() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/login")
                .param("username", "admin")
                .param("password", ""))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Powinno zalogować się z poprawnym admin hasłem")
    void testLoginWithValidAdminCredentials() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/login")
                .param("username", "admin")
                .param("password", "admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("Powinno zalogować się z poprawnym user hasłem")
    void testLoginWithValidUserCredentials() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/login")
                .param("username", "user")
                .param("password", "user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("Powinno odrzucić login z błędnym hasłem")
    void testLoginWithInvalidPassword() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/login")
                .param("username", "admin")
                .param("password", "wrongpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login?error"));
    }

    @Test
    @DisplayName("Powinno odrzucić login z nieistniejącym użytkownikiem")
    void testLoginWithNonExistentUser() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/login")
                .param("username", "nonexistent")
                .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login?error"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Powinno zezwolić dostęp do dashboard dla zalogowanego")
    void testDashboardAccessForAuthenticatedUser() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Powinno wylogować użytkownika")
    void testLogout() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login?logout"));
    }

    @Test
    @DisplayName("Powinno zabronić dostępu do add-statement dla niezalogowanego")
    void testAddStatementAccessDeniedForUnauthenticated() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/add-statement"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Powinno zabronić dostępu do add-statement dla zwykłego użytkownika")
    void testAddStatementAccessDeniedForUser() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/add-statement"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Powinno zezwolić dostęp do add-statement dla admina")
    void testAddStatementAccessAllowedForAdmin() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/add-statement"))
                .andExpect(status().isOk());
    }
}


