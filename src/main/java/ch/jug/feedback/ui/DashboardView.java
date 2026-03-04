package ch.jug.feedback.ui;

import ch.jug.feedback.model.FeedbackForm;
import ch.jug.feedback.model.FormStatus;
import ch.jug.feedback.service.FormService;
import ch.jug.feedback.service.QrCodeService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinSession;

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard - JUG Feedback")
public class DashboardView extends VerticalLayout implements BeforeEnterObserver {

    private final FormService formService;
    private final QrCodeService qrCodeService;

    private Grid<FeedbackForm> grid;

    public DashboardView(FormService formService, QrCodeService qrCodeService) {
        this.formService = formService;
        this.qrCodeService = qrCodeService;

        setSizeFull();
        setPadding(true);

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);

        H2 title = new H2("Feedback Formulare");
        title.getStyle().set("margin", "0");

        Button createButton = new Button("Neues Formular", VaadinIcon.PLUS.create());
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createButton.addClickListener(e -> UI.getCurrent().navigate(FormEditorView.class));

        header.add(title, createButton);
        header.expand(title);

        grid = createGrid();

        add(header, grid);
        expand(grid);

        refreshGrid();
    }

    private Grid<FeedbackForm> createGrid() {
        Grid<FeedbackForm> g = new Grid<>(FeedbackForm.class, false);
        g.setSizeFull();

        g.addColumn(FeedbackForm::getTitle).setHeader("Titel").setAutoWidth(true).setFlexGrow(1);
        g.addColumn(FeedbackForm::getSpeakerName).setHeader("Referent").setAutoWidth(true);
        g.addColumn(FeedbackForm::getTalkTitle).setHeader("Thema").setAutoWidth(true).setFlexGrow(1);
        g.addColumn(form -> form.getStatus().name()).setHeader("Status").setAutoWidth(true)
                .setPartNameGenerator(form -> "status-" + form.getStatus().name().toLowerCase());
        g.addColumn(form -> form.getResponseCount() + " Antworten").setHeader("Antworten").setAutoWidth(true);
        g.addColumn(form -> {
            if (form.getCreatedAt() != null) {
                return form.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            }
            return "";
        }).setHeader("Erstellt am").setAutoWidth(true);

        g.addComponentColumn(this::createActionButtons).setHeader("Aktionen").setAutoWidth(true);

        return g;
    }

    private Component createActionButtons(FeedbackForm form) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setPadding(false);
        layout.setSpacing(true);

        Button editButton = new Button(VaadinIcon.EDIT.create());
        editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        editButton.getElement().setAttribute("title", "Bearbeiten");
        editButton.addClickListener(e ->
                UI.getCurrent().navigate(FormEditorView.class, form.getId()));
        editButton.setEnabled(form.getStatus() == FormStatus.DRAFT);

        Button qrButton = new Button(VaadinIcon.QRCODE.create());
        qrButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        qrButton.getElement().setAttribute("title", "QR Code anzeigen");
        qrButton.addClickListener(e -> showQrCodeDialog(form));
        qrButton.setEnabled(form.getStatus() == FormStatus.PUBLIC);

        Button resultsButton = new Button(VaadinIcon.CHART.create());
        resultsButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        resultsButton.getElement().setAttribute("title", "Ergebnisse anzeigen");
        resultsButton.addClickListener(e ->
                UI.getCurrent().navigate(ResultsView.class, form.getId()));

        Button publishButton = new Button(VaadinIcon.GLOBE.create());
        publishButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
        publishButton.getElement().setAttribute("title", "Veröffentlichen");
        publishButton.addClickListener(e -> {
            formService.publish(form.getId());
            refreshGrid();
            Notification notification = Notification.show("Formular veröffentlicht", 3000,
                    Notification.Position.BOTTOM_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        publishButton.setVisible(form.getStatus() == FormStatus.DRAFT);

        Button closeButton = new Button(VaadinIcon.LOCK.create());
        closeButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
        closeButton.getElement().setAttribute("title", "Schließen");
        closeButton.addClickListener(e -> {
            formService.close(form.getId());
            refreshGrid();
            Notification.show("Formular geschlossen", 3000,
                    Notification.Position.BOTTOM_CENTER);
        });
        closeButton.setVisible(form.getStatus() == FormStatus.PUBLIC);

        Button reopenButton = new Button(VaadinIcon.UNLOCK.create());
        reopenButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        reopenButton.getElement().setAttribute("title", "Wieder öffnen");
        reopenButton.addClickListener(e -> {
            formService.reopen(form.getId());
            refreshGrid();
            Notification notification = Notification.show("Formular wieder geöffnet", 3000,
                    Notification.Position.BOTTOM_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        reopenButton.setVisible(form.getStatus() == FormStatus.CLOSED);

        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        deleteButton.getElement().setAttribute("title", "Löschen");
        deleteButton.addClickListener(e -> {
            ConfirmDialog dialog = new ConfirmDialog(
                    "Formular löschen",
                    "Möchten Sie das Formular '" + form.getTitle() + "' wirklich löschen? Alle Antworten werden ebenfalls gelöscht.",
                    "Löschen", confirmEvent -> {
                formService.deleteForm(form.getId());
                refreshGrid();
                Notification.show("Formular gelöscht", 3000,
                        Notification.Position.BOTTOM_CENTER);
            },
                    "Abbrechen", cancelEvent -> {
            });
            dialog.setConfirmButtonTheme("error primary");
            dialog.open();
        });

        layout.add(editButton, qrButton, resultsButton, publishButton, closeButton, reopenButton, deleteButton);
        return layout;
    }

    private void showQrCodeDialog(FeedbackForm form) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("QR Code - " + form.getTitle());

        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(Alignment.CENTER);

        String baseUrl = getBaseUrl();
        String formUrl = baseUrl + "/form/" + form.getPublicToken();

        Paragraph urlParagraph = new Paragraph(formUrl);
        urlParagraph.getStyle().set("word-break", "break-all").set("font-size", "0.85rem");

        try {
            byte[] qrCodeBytes = qrCodeService.generateQrCode(formUrl);
            StreamResource resource = new StreamResource("qr-code.png",
                    () -> new ByteArrayInputStream(qrCodeBytes));
            Image qrImage = new Image(resource, "QR Code");
            qrImage.setWidth("300px");
            qrImage.setHeight("300px");
            content.add(qrImage);
        } catch (Exception e) {
            content.add(new Paragraph("QR Code konnte nicht generiert werden: " + e.getMessage()));
        }

        content.add(new H4("Formular URL:"), urlParagraph);

        Button copyButton = new Button("URL kopieren", VaadinIcon.COPY.create());
        copyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        copyButton.addClickListener(e ->
                UI.getCurrent().getPage().executeJs(
                        "navigator.clipboard.writeText($0)", formUrl));
        content.add(copyButton);

        dialog.add(content);
        dialog.setWidth("400px");

        Button closeButton = new Button("Schließen");
        closeButton.addClickListener(e -> dialog.close());
        dialog.getFooter().add(closeButton);

        dialog.open();
    }

    private String getBaseUrl() {
        VaadinRequest request = VaadinRequest.getCurrent();
        if (request != null) {
            String serverName = request.getServerName();
            int port = request.getServerPort();
            String scheme = port == 443 ? "https" : "http";
            if ((scheme.equals("http") && port == 80) || (scheme.equals("https") && port == 443)) {
                return scheme + "://" + serverName;
            }
            return scheme + "://" + serverName + ":" + port;
        }
        return "http://localhost:8080";
    }

    private void refreshGrid() {
        List<FeedbackForm> forms = formService.getAllForms();
        grid.setItems(forms);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Boolean authenticated = (Boolean) VaadinSession.getCurrent().getAttribute("authenticated");
        if (!Boolean.TRUE.equals(authenticated)) {
            event.forwardTo(LoginView.class);
        }
    }
}
