package pl.ostropa.lesnazrzutka.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.ostropa.lesnazrzutka.service.BankStatementService;

import java.text.NumberFormat;
import java.util.Locale;

@Route("")
@PageTitle("Dashboard - Zarządzanie Wyciągami Bankowych")
public class DashboardView extends VerticalLayout {

    private final BankStatementService bankStatementService;

    public DashboardView(BankStatementService bankStatementService) {
        this.bankStatementService = bankStatementService;

        setSpacing(true);
        setPadding(true);
        setSizeFull();

        // Header
        add(createHeader());

        // Main content area
        add(createMainContent());
    }

    private VerticalLayout createHeader() {
        VerticalLayout header = new VerticalLayout();
        header.setSpacing(false);
        header.setPadding(false);

        // Top bar with title and logout button
        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidthFull();
        topBar.setAlignItems(FlexComponent.Alignment.CENTER);
        topBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "Użytkownik";

        H1 title = new H1("📊 Zarządzanie Wyciągami Bankowymi");
        title.getStyle().set("margin", "0");

        // Logout button
        Button logoutButton = new Button("Wyloguj się");
        logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        logoutButton.addClickListener(event -> {
            getUI().ifPresent(ui -> {
                ui.getPage().setLocation("/logout");
            });
        });

        topBar.add(title, logoutButton);

        Paragraph subtitle = new Paragraph("Witaj, " + username + "! Tutaj możesz zarządzać wyciągami z banku Santander.");
        subtitle.getStyle().set("color", "#666").set("font-size", "14px").set("margin", "0");

        header.add(topBar, subtitle);
        return header;
    }

    private HorizontalLayout createMainContent() {
        HorizontalLayout mainContent = new HorizontalLayout();
        mainContent.setWidthFull();
        mainContent.setSpacing(true);

        // Left panel - Actions
        VerticalLayout actionsPanel = createActionsPanel();

        // Right panel - Account info
        VerticalLayout accountPanel = createAccountPanel();

        mainContent.add(actionsPanel, accountPanel);
        mainContent.setFlexGrow(1, actionsPanel);
        mainContent.setFlexGrow(1, accountPanel);

        return mainContent;
    }

    private VerticalLayout createActionsPanel() {
        VerticalLayout panel = new VerticalLayout();
        panel.setClassName("actions-panel");
        panel.getStyle()
                .set("border", "1px solid #ddd")
                .set("border-radius", "8px")
                .set("padding", "20px")
                .set("background-color", "#f9f9f9");

        H2 title = new H2("⚙️ Akcje");
        title.getStyle().set("margin-top", "0");

        Button addStatementButton = new Button("Dodaj Wyciąg Bankowy");
        addStatementButton.setIcon(VaadinIcon.PLUS.create());
        addStatementButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addStatementButton.setWidth("100%");
        addStatementButton.addClickListener(event ->
            getUI().ifPresent(ui -> ui.navigate("add-statement"))
        );

        Button viewHistoryButton = new Button("Przeglądaj Historię");
        viewHistoryButton.setIcon(VaadinIcon.LIST.create());
        viewHistoryButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        viewHistoryButton.setWidth("100%");
        viewHistoryButton.addClickListener(event ->
            getUI().ifPresent(ui -> ui.navigate("history"))
        );

        Button viewAccountsButton = new Button("Przeglądaj Konta");
        viewAccountsButton.setIcon(VaadinIcon.WALLET.create());
        viewAccountsButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        viewAccountsButton.setWidth("100%");
        viewAccountsButton.addClickListener(event ->
            getUI().ifPresent(ui -> ui.navigate("accounts"))
        );

        panel.add(title, new Paragraph("Wybierz akcję:"), addStatementButton, viewHistoryButton, viewAccountsButton);
        return panel;
    }

    private VerticalLayout createAccountPanel() {
        VerticalLayout panel = new VerticalLayout();
        panel.setClassName("account-panel");
        panel.getStyle()
                .set("border", "1px solid #ddd")
                .set("border-radius", "8px")
                .set("padding", "20px")
                .set("background-color", "#f0f8ff");

        H2 title = new H2("💳 Informacje o Kontach");
        title.getStyle().set("margin-top", "0");

        // Get latest statement with balance info
        var statements = bankStatementService.getAllBankStatements();
        if (!statements.isEmpty()) {
            // Group by account number and get latest
            var latestByAccount = statements.stream()
                    .filter(s -> s.getAccountNumber() != null && s.getAccountBalance() != null)
                    .toList();

            if (!latestByAccount.isEmpty()) {
                VerticalLayout accountsList = new VerticalLayout();
                accountsList.setSpacing(true);

                latestByAccount.forEach(statement -> {
                    HorizontalLayout accountRow = new HorizontalLayout();
                    accountRow.getStyle()
                            .set("border", "1px solid #ccc")
                            .set("padding", "10px")
                            .set("border-radius", "4px")
                            .set("background-color", "white");

                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("pl", "PL"));
                    String formattedBalance = currencyFormat.format(statement.getAccountBalance());

                    VerticalLayout accountInfo = new VerticalLayout();
                    accountInfo.setSpacing(false);
                    accountInfo.setPadding(false);
                    Paragraph accountLabel = new Paragraph("Numer konta: " + statement.getAccountNumber());
                    accountLabel.getStyle().set("font-weight", "bold");
                    Paragraph balance = new Paragraph("Saldo: " + formattedBalance);
                    balance.getStyle().set("color", statement.getAccountBalance() >= 0 ? "#2e7d32" : "#c62828");
                    accountInfo.add(accountLabel, balance);

                    accountRow.add(accountInfo);
                    accountRow.setFlexGrow(1, accountInfo);
                    accountsList.add(accountRow);
                });

                panel.add(title, new Paragraph("Ostatnie wyciągi z informacją o saldzie:"), accountsList);
            } else {
                panel.add(title, new Paragraph("Brak wyciągów z informacją o saldzie."));
            }
        } else {
            panel.add(title, new Paragraph("Brak wrzuconych wyciągów. Dodaj pierwszy wyciąg, aby zobaczyć saldo."));
        }

        return panel;
    }
}

