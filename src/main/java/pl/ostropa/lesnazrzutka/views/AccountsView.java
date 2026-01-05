package pl.ostropa.lesnazrzutka.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pl.ostropa.lesnazrzutka.model.BankStatement;
import pl.ostropa.lesnazrzutka.service.BankStatementService;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;

@Route("accounts")
@PageTitle("Przeglądaj Konta")
public class AccountsView extends VerticalLayout {

    private final BankStatementService bankStatementService;
    private final Grid<BankStatement> grid = new Grid<>(BankStatement.class, false);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AccountsView(BankStatementService bankStatementService) {
        this.bankStatementService = bankStatementService;

        setSpacing(true);
        setPadding(true);
        setSizeFull();

        add(new H1("💳 Przegląd Kont Bankowych"));

        configureGrid();
        add(grid);

        // Back button
        Button backButton = new Button("Wróć do Dashboard");
        backButton.setIcon(VaadinIcon.ARROW_LEFT.create());
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(event ->
            getUI().ifPresent(ui -> ui.navigate(""))
        );
        add(backButton);

        refreshGrid();
    }

    private void configureGrid() {
        grid.addColumn(BankStatement::getAccountNumber).setHeader("Numer Konta").setAutoWidth(true);
        grid.addColumn(bs -> {
            if (bs.getAccountBalance() == null) return "-";
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("pl", "PL"));
            return currencyFormat.format(bs.getAccountBalance());
        }).setHeader("Saldo").setAutoWidth(true);
        grid.addColumn(BankStatement::getBankName).setHeader("Bank").setAutoWidth(true);
        grid.addColumn(BankStatement::getFileName).setHeader("Plik Wyciągu").setAutoWidth(true);
        grid.addColumn(bs -> bs.getUploadedDate().format(dateFormatter)).setHeader("Data Wrzucenia").setAutoWidth(true);
        grid.addColumn(BankStatement::getUploadedBy).setHeader("Wrzucił").setAutoWidth(true);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
    }

    private void refreshGrid() {
        var statements = bankStatementService.getAllBankStatements()
                .stream()
                .filter(s -> s.getAccountNumber() != null && s.getAccountBalance() != null)
                .collect(Collectors.toList());
        grid.setItems(statements);
    }
}

