package pl.ostropa.lesnazrzutka.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.ostropa.lesnazrzutka.model.BankStatement;
import pl.ostropa.lesnazrzutka.service.BankStatementService;
import pl.ostropa.lesnazrzutka.logging.AppLogger;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route("history")
@PageTitle("Historia Wyciągów Bankowych")
@SuppressWarnings("removal")
public class BankStatementUploadView extends VerticalLayout {

    private static final AppLogger logger = AppLogger.getLogger(BankStatementUploadView.class);
    private final BankStatementService bankStatementService;
    private final Grid<BankStatement> grid = new Grid<>(BankStatement.class, false);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public BankStatementUploadView(BankStatementService bankStatementService) {
        this.bankStatementService = bankStatementService;

        setSpacing(true);
        setPadding(true);
        setSizeFull();

        // Header
        add(new H1("📋 Wrzucanie Wyciągów Bankowych"));

        // Upload section
        add(createUploadSection());

        // Divider
        add(new Paragraph("───────────────────────────────────"));

        // Grid with statements
        add(new H1("📊 Historia Wyciągów"));
        configureGrid();
        add(grid);

        // Load data
        refreshGrid();
    }

    private VerticalLayout createUploadSection() {
        VerticalLayout section = new VerticalLayout();
        section.setClassName("upload-section");
        section.getStyle().set("border", "1px solid #ccc").set("border-radius", "8px").set("padding", "20px");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("application/pdf", ".pdf", ".csv", ".xls", ".xlsx", "text/csv",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        upload.setMaxFileSize(10485760); // 10MB
        upload.setDropLabelIcon(VaadinIcon.CLOUD_UPLOAD.create());

        TextArea description = new TextArea();
        description.setPlaceholder("Dodaj opis wyciągu (opcjonalnie)");
        description.setWidth("100%");
        description.setMaxLength(500);

        Button uploadButton = new Button("Wrzuć Wyciąg");
        uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        uploadButton.setIcon(VaadinIcon.CLOUD_UPLOAD.create());

        upload.addFileRejectedListener(event ->
            Notification.show("❌ Plik został odrzucony: " + event.getErrorMessage(), 3000, Notification.Position.TOP_CENTER)
        );

        upload.addSucceededListener(event -> {
            try {
                String fileName = event.getFileName();
                InputStream inputStream = buffer.getInputStream();
                byte[] fileBytes = inputStream.readAllBytes();

                BankStatement statement = new BankStatement();
                statement.setFileName(fileName);
                statement.setFileContent(fileBytes);
                statement.setFileSize((long) fileBytes.length);
                statement.setFileType(event.getMIMEType());
                statement.setBankName("Bank");
                statement.setDescription(description.getValue().isEmpty() ? null : description.getValue());

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.getName() != null) {
                    statement.setUploadedBy(auth.getName());
                }

                bankStatementService.saveBankStatement(statement);

                Notification.show("✅ Wyciąg został wrzucony pomyślnie!", 3000, Notification.Position.TOP_CENTER);
                description.clear();
                refreshGrid();

            } catch (Exception e) {
                logger.error("Error uploading file", e);
                Notification.show("❌ Błąd podczas wrzucania pliku: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            }
        });

        upload.addFailedListener(event ->
            Notification.show("❌ Błąd podczas przesyłania: " + event.getReason().getMessage(), 3000, Notification.Position.TOP_CENTER)
        );

        HorizontalLayout uploadLayout = new HorizontalLayout(upload);
        uploadLayout.setWidthFull();

        section.add(
                new Paragraph("Wrzuć wyciąg bankowy. Obsługiwane formaty: PDF, CSV, XLS, XLSX (maks. 10MB)"),
                uploadLayout,
                description
        );

        return section;
    }

    private void configureGrid() {
        grid.addColumn(BankStatement::getFileName).setHeader("Nazwa Pliku").setAutoWidth(true);
        grid.addColumn(BankStatement::getBankName).setHeader("Bank").setAutoWidth(true);
        grid.addColumn(bs -> bs.getFileSize() / 1024 + " KB").setHeader("Rozmiar").setAutoWidth(true);
        grid.addColumn(bs -> bs.getUploadedDate().format(dateFormatter)).setHeader("Data Wrzucenia").setAutoWidth(true);
        grid.addColumn(BankStatement::getUploadedBy).setHeader("Wrzucił").setAutoWidth(true);
        grid.addColumn(BankStatement::getDescription).setHeader("Opis").setAutoWidth(true);
        grid.addColumn(bs -> bs.isProcessed() ? "✅ Tak" : "❌ Nie").setHeader("Przetworzony").setAutoWidth(true);
        grid.addColumn(bs -> bs.getProcessedDate() != null ? bs.getProcessedDate().format(dateFormatter) : "-").setHeader("Data Przetworzenia").setAutoWidth(true);

        // Actions column
        grid.addComponentColumn(statement ->
                new HorizontalLayout(
                        createDownloadButton(statement),
                        createDeleteButton(statement),
                        createProcessButton(statement)
                )
        ).setHeader("Akcje").setAutoWidth(true);

        grid.setSelectionMode(Grid.SelectionMode.NONE);
    }

    private Button createDownloadButton(BankStatement statement) {
        Button button = new Button("Pobierz");
        button.setIcon(VaadinIcon.DOWNLOAD.create());
        button.addThemeVariants(ButtonVariant.LUMO_SMALL);
        button.addClickListener(clickEvent ->
            Notification.show("Pobieranie pliku: " + statement.getFileName(), 2000, Notification.Position.BOTTOM_CENTER)
        );
        return button;
    }

    private Button createDeleteButton(BankStatement statement) {
        Button button = new Button("Usuń");
        button.setIcon(VaadinIcon.TRASH.create());
        button.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
        button.addClickListener(clickEvent -> {
            Dialog confirmDialog = new Dialog();
            confirmDialog.setHeaderTitle("Potwierdzenie usunięcia");
            confirmDialog.add(new Paragraph("Czy na pewno chcesz usunąć plik: " + statement.getFileName() + "?"));

            Button confirmButton = new Button("Usuń", event -> {
                bankStatementService.deleteBankStatement(statement.getId());
                Notification.show("✅ Plik został usunięty!", 2000, Notification.Position.BOTTOM_CENTER);
                confirmDialog.close();
                refreshGrid();
            });
            confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

            Button cancelButton = new Button("Anuluj", event -> confirmDialog.close());
            confirmDialog.getFooter().add(cancelButton, confirmButton);

            confirmDialog.open();
        });
        return button;
    }

    private Button createProcessButton(BankStatement statement) {
        Button button = new Button(statement.isProcessed() ? "Przetworzony" : "Oznacz jako przetworzony");
        button.setIcon(VaadinIcon.CHECK.create());
        button.addThemeVariants(ButtonVariant.LUMO_SMALL);
        if (statement.isProcessed()) {
            button.setEnabled(false);
        } else {
            button.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        }
        button.addClickListener(clickEvent -> {
            bankStatementService.markAsProcessed(statement.getId());
            Notification.show("✅ Wyciąg został oznaczony jako przetworzony!", 2000, Notification.Position.BOTTOM_CENTER);
            refreshGrid();
        });
        return button;
    }

    private void refreshGrid() {
        List<BankStatement> statements = bankStatementService.getAllBankStatements();
        grid.setItems(statements);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // Add back button after component is attached
        if (getComponentCount() > 0) {
            Button backButton = new Button("Wróć do Dashboard");
            backButton.setIcon(VaadinIcon.ARROW_LEFT.create());
            backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            backButton.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate("")));
            addComponentAsFirst(backButton);
        }
    }
}


