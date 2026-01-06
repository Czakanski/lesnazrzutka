package pl.ostropa.lesnazrzutka.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.annotation.Secured;
import pl.ostropa.lesnazrzutka.model.BankStatement;
import pl.ostropa.lesnazrzutka.service.BankStatementService;
import pl.ostropa.lesnazrzutka.logging.AppLogger;

import java.io.InputStream;

@Route("add-statement")
@PageTitle("Dodaj Wyciąg Bankowy")
@Secured("ROLE_ADMIN")
@SuppressWarnings("removal")
public class AddBankStatementView extends VerticalLayout {

    private static final AppLogger logger = AppLogger.getLogger(AddBankStatementView.class);
    private final BankStatementService bankStatementService;

    public AddBankStatementView(BankStatementService bankStatementService) {
        this.bankStatementService = bankStatementService;

        setSpacing(true);
        setPadding(true);
        setSizeFull();

        add(new H1("➕ Dodaj Nowy Wyciąg Bankowy"));
        add(createFormSection());

        // Back button
        Button backButton = new Button("Wróć do Dashboard");
        backButton.setIcon(VaadinIcon.ARROW_LEFT.create());
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(event ->
            getUI().ifPresent(ui -> ui.navigate(""))
        );
        add(backButton);
    }

    private VerticalLayout createFormSection() {
        VerticalLayout section = new VerticalLayout();
        section.setClassName("form-section");
        section.getStyle()
                .set("border", "1px solid #ccc")
                .set("border-radius", "8px")
                .set("padding", "20px")
                .set("max-width", "600px");

        // File upload section
        VerticalLayout uploadArea = new VerticalLayout();
        uploadArea.getStyle()
                .set("border", "2px dashed #999")
                .set("border-radius", "8px")
                .set("padding", "20px")
                .set("text-align", "center")
                .set("background-color", "#fafafa");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("application/pdf", ".pdf", ".csv", ".xls", ".xlsx", "text/csv",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        upload.setMaxFileSize(10485760); // 10MB

        Paragraph uploadLabel = new Paragraph("📤 Przeciągnij plik tutaj lub kliknij, aby wybrać");
        uploadLabel.getStyle().set("margin", "0");

        uploadArea.add(uploadLabel, upload);

        // Form fields
        TextField accountNumberField = new TextField();
        accountNumberField.setLabel("Numer Konta Bankowego");
        accountNumberField.setPlaceholder("np. 12 3456 7890 1234 5678 9012 3456");
        accountNumberField.setWidth("100%");

        NumberField balanceField = new NumberField();
        balanceField.setLabel("Saldo Konta (PLN)");
        balanceField.setPlaceholder("0.00");
        balanceField.setWidth("100%");
        balanceField.setMin(-1000000);
        balanceField.setMax(1000000);

        TextArea descriptionField = new TextArea();
        descriptionField.setLabel("Opis (opcjonalnie)");
        descriptionField.setPlaceholder("Dodaj notatki dotyczące tego wyciągu");
        descriptionField.setWidth("100%");
        descriptionField.setMaxLength(500);
        descriptionField.setHeight("100px");

        // Submit button
        Button submitButton = new Button("Wrzuć Wyciąg");
        submitButton.setIcon(VaadinIcon.CLOUD_UPLOAD.create());
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.setWidth("100%");

        submitButton.addClickListener(evt -> {
            if (!validateForm(buffer, accountNumberField, balanceField)) {
                return;
            }

            try {
                handleFileUpload(buffer, accountNumberField, balanceField, descriptionField);
            } catch (Exception e) {
                logger.error("Error uploading statement", e);
                Notification.show("❌ Błąd: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            }
        });

        section.add(
                new Paragraph("Wybierz plik wyciągu oraz uzupełnij informacje o koncie:"),
                uploadArea,
                accountNumberField,
                balanceField,
                descriptionField,
                submitButton
        );

        return section;
    }

    private boolean validateForm(MemoryBuffer buffer, TextField accountNumberField, NumberField balanceField) {
        if (buffer.getFileName() == null || buffer.getFileName().isEmpty()) {
            Notification.show("❌ Proszę wybrać plik wyciągu", 3000, Notification.Position.TOP_CENTER);
            return false;
        }

        if (accountNumberField.getValue() == null || accountNumberField.getValue().isEmpty()) {
            Notification.show("❌ Proszę podać numer konta", 3000, Notification.Position.TOP_CENTER);
            return false;
        }

        if (balanceField.getValue() == null) {
            Notification.show("❌ Proszę podać saldo konta", 3000, Notification.Position.TOP_CENTER);
            return false;
        }

        return true;
    }

    private void handleFileUpload(MemoryBuffer buffer, TextField accountNumberField, NumberField balanceField, TextArea descriptionField) throws Exception {
        InputStream inputStream = buffer.getInputStream();
        byte[] fileBytes = inputStream.readAllBytes();

        BankStatement statement = new BankStatement();
        statement.setFileName(buffer.getFileName());
        statement.setFileContent(fileBytes);
        statement.setFileSize((long) fileBytes.length);
        statement.setFileType("application/pdf"); // Default type
        statement.setBankName("Bank");
        statement.setAccountNumber(accountNumberField.getValue());
        statement.setAccountBalance(balanceField.getValue());
        statement.setDescription(descriptionField.getValue().isEmpty() ? null : descriptionField.getValue());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            statement.setUploadedBy(auth.getName());
        }

        bankStatementService.saveBankStatement(statement);

        Notification.show("✅ Wyciąg został pomyślnie dodany!", 3000, Notification.Position.TOP_CENTER);

        // Clear form
        accountNumberField.clear();
        balanceField.clear();
        descriptionField.clear();

        // Redirect after 1 second
        getUI().ifPresent(ui -> {
            try {
                Thread.sleep(1000);
                ui.navigate(DashboardView.class);
            } catch (InterruptedException e) {
                logger.error("Error redirecting", e);
            }
        });
    }
}

