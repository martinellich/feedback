package ch.jug.feedback.ui;

import ch.jug.feedback.service.TokenService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login - JUG Feedback")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final TokenService tokenService;

    public LoginView(TokenService tokenService) {
        this.tokenService = tokenService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout card = new VerticalLayout();
        card.setWidth("400px");
        card.setPadding(true);
        card.addClassName("login-card");

        H1 title = new H1("JUG Feedback");
        title.getStyle().set("margin", "0");

        H2 subtitle = new H2("Anmeldung");
        subtitle.getStyle().set("color", "var(--lumo-secondary-text-color)").set("font-size", "1.2rem").set("margin-top", "0.5rem");

        Paragraph description = new Paragraph(
                "Bitte geben Sie Ihr Einmal-Zugriffstoken ein, um sich anzumelden.");
        description.getStyle().set("color", "var(--lumo-secondary-text-color)");

        TextField tokenField = new TextField("Zugriffstoken");
        tokenField.setWidthFull();
        tokenField.setPlaceholder("Ihr Token hier eingeben...");
        tokenField.setAutofocus(true);

        Button loginButton = new Button("Anmelden");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.setWidthFull();
        loginButton.addClickListener(e -> login(tokenField.getValue()));

        tokenField.addKeyPressListener(com.vaadin.flow.component.Key.ENTER,
                e -> login(tokenField.getValue()));

        card.add(title, subtitle, description, tokenField, loginButton);
        add(card);
    }

    private void login(String tokenValue) {
        if (tokenValue == null || tokenValue.isBlank()) {
            Notification.show("Bitte geben Sie Ihr Token ein", 3000,
                    Notification.Position.MIDDLE);
            return;
        }

        if (tokenService.validateAndConsumeToken(tokenValue.trim())) {
            VaadinSession.getCurrent().setAttribute("authenticated", true);
            UI.getCurrent().navigate(DashboardView.class);
        } else {
            Notification notification = Notification.show(
                    "Ungültiges oder bereits verwendetes Token", 3000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Boolean authenticated = (Boolean) VaadinSession.getCurrent().getAttribute("authenticated");
        if (Boolean.TRUE.equals(authenticated)) {
            event.forwardTo(DashboardView.class);
        }
    }
}
