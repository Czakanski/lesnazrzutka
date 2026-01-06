package pl.ostropa.lesnazrzutka.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
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
import pl.ostropa.lesnazrzutka.model.Transaction;
import pl.ostropa.lesnazrzutka.service.TransactionService;
import pl.ostropa.lesnazrzutka.service.TransactionService.TransactionGroupData;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@Route("transactions")
@PageTitle("Wpłaty - Zarządzanie Wyciągami Bankowych")
public class TransactionsView extends VerticalLayout {

    private final TransactionService transactionService;

    public TransactionsView(TransactionService transactionService) {
        this.transactionService = transactionService;

        setSpacing(true);
        setPadding(true);
        setSizeFull();

        // Header
        add(createHeader());

        // Main content
        add(createMainContent());
    }

    private VerticalLayout createHeader() {
        VerticalLayout header = new VerticalLayout();
        header.setSpacing(false);
        header.setPadding(false);

        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidthFull();
        topBar.setAlignItems(FlexComponent.Alignment.CENTER);
        topBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "Użytkownik";

        H1 title = new H1("💰 Listowanie Wpłat");
        title.getStyle().set("margin", "0");

        Button backButton = new Button("Wróć do Dashboardu");
        backButton.setIcon(VaadinIcon.ARROW_BACKWARD.create());
        backButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_SMALL);
        backButton.addClickListener(event ->
            getUI().ifPresent(ui -> ui.navigate(""))
        );

        topBar.add(title, backButton);

        Paragraph subtitle = new Paragraph("Przeglądaj wpłaty pogrupowane po koncie źródłowym");
        subtitle.getStyle().set("color", "#666").set("font-size", "14px").set("margin", "0");

        header.add(topBar, subtitle);
        return header;
    }

    private VerticalLayout createMainContent() {
        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setSpacing(true);
        mainContent.setPadding(false);

        // Get grouped transactions
        Map<String, TransactionGroupData> groupedTransactions = transactionService.getIncomingTransactionsGroupedWithSum();

        if (groupedTransactions.isEmpty()) {
            Paragraph noData = new Paragraph("Brak wpłat do wyświetlenia.");
            noData.getStyle().set("color", "#999").set("font-style", "italic");
            mainContent.add(noData);
        } else {
            // Summary section
            mainContent.add(createSummarySection(groupedTransactions));

            // Grouped transactions
            groupedTransactions.forEach((accountNumber, groupData) -> {
                mainContent.add(createAccountGroup(accountNumber, groupData));
            });
        }

        return mainContent;
    }

    private VerticalLayout createSummarySection(Map<String, TransactionGroupData> groupedTransactions) {
        VerticalLayout summaryLayout = new VerticalLayout();
        summaryLayout.setClassName("actions-panel");
        summaryLayout.getStyle()
                .set("border", "1px solid #ddd")
                .set("border-radius", "8px")
                .set("padding", "20px")
                .set("background-color", "#f9f9f9")
                .set("margin-bottom", "20px");

        H2 title = new H2("📊 Podsumowanie");
        title.getStyle().set("margin-top", "0");

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("pl", "PL"));

        Double totalSum = groupedTransactions.values().stream()
                .mapToDouble(TransactionGroupData::getTotalAmount)
                .sum();

        int totalTransactions = groupedTransactions.values().stream()
                .mapToInt(TransactionGroupData::getTransactionCount)
                .sum();

        int totalAccounts = groupedTransactions.size();

        HorizontalLayout summaryRow = new HorizontalLayout();
        summaryRow.setSpacing(true);
        summaryRow.setWidthFull();

        // Total accounts
        VerticalLayout accountsCard = createSummaryCard(
                "Liczba kont źródłowych",
                String.valueOf(totalAccounts),
                "#2d7a4a"
        );

        // Total transactions
        VerticalLayout transactionsCard = createSummaryCard(
                "Liczba wpłat",
                String.valueOf(totalTransactions),
                "#4caf50"
        );

        // Total amount
        VerticalLayout amountCard = createSummaryCard(
                "Suma wszystkich wpłat",
                currencyFormat.format(totalSum),
                "#1e5631"
        );

        summaryRow.add(accountsCard, transactionsCard, amountCard);
        summaryRow.setFlexGrow(1, accountsCard);
        summaryRow.setFlexGrow(1, transactionsCard);
        summaryRow.setFlexGrow(1, amountCard);

        summaryLayout.add(title, summaryRow);
        return summaryLayout;
    }

    private VerticalLayout createSummaryCard(String label, String value, String color) {
        VerticalLayout card = new VerticalLayout();
        card.getStyle()
                .set("background", "white")
                .set("border", "2px solid " + color)
                .set("border-radius", "6px")
                .set("padding", "15px")
                .set("text-align", "center")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)");
        card.setSpacing(false);
        card.setAlignItems(FlexComponent.Alignment.CENTER);

        Paragraph labelPara = new Paragraph(label);
        labelPara.getStyle()
                .set("font-size", "12px")
                .set("color", "#666")
                .set("margin", "0 0 8px 0");

        Paragraph valuePara = new Paragraph(value);
        valuePara.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("color", color)
                .set("margin", "0");

        card.add(labelPara, valuePara);
        return card;
    }

    private VerticalLayout createAccountGroup(String accountNumber, TransactionGroupData groupData) {
        VerticalLayout groupLayout = new VerticalLayout();
        groupLayout.setClassName("actions-panel");
        groupLayout.getStyle()
                .set("border", "2px solid #a5d6a7")
                .set("border-radius", "8px")
                .set("padding", "20px")
                .set("background-color", "#f5f9f7")
                .set("margin-bottom", "20px");

        // Header with account number and total
        HorizontalLayout groupHeader = new HorizontalLayout();
        groupHeader.setWidthFull();
        groupHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        groupHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        H2 accountTitle = new H2("🏦 Konto: " + accountNumber);
        accountTitle.getStyle().set("margin", "0").set("color", "#2d7a4a");

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("pl", "PL"));
        String totalAmount = currencyFormat.format(groupData.getTotalAmount());

        Paragraph totalLabel = new Paragraph("Suma: " + totalAmount);
        totalLabel.getStyle()
                .set("font-size", "18px")
                .set("font-weight", "bold")
                .set("color", "#1e5631")
                .set("margin", "0");

        groupHeader.add(accountTitle, totalLabel);

        groupLayout.add(groupHeader);

        // Transactions grid
        Grid<Transaction> grid = new Grid<>();
        grid.setItems(groupData.getTransactions());
        grid.setWidthFull();
        grid.addColumn(Transaction::getToAccountNumber)
                .setHeader("Konto docelowe")
                .setFlexGrow(1);
        grid.addColumn(t -> currencyFormat.format(t.getAmount()))
                .setHeader("Kwota")
                .setFlexGrow(0);
        grid.addColumn(t -> t.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .setHeader("Data transakcji")
                .setFlexGrow(0);
        grid.addColumn(Transaction::getDescription)
                .setHeader("Opis")
                .setFlexGrow(1);
        grid.addColumn(Transaction::getReference)
                .setHeader("Referencja")
                .setFlexGrow(0);

        groupLayout.add(grid);

        return groupLayout;
    }
}

