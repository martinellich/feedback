package ch.martinelli.jug.feedback.views;

import ch.martinelli.jug.feedback.service.TokenService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.List;

@Route("login")
@PageTitle("Login - JUG Feedback")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final TokenService tokenService;
    private final TextField tokenField = new TextField("Access Token");

    public LoginView(TokenService tokenService) {
        this.tokenService = tokenService;

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        H2 title = new H2("JUG Feedback Login");
        tokenField.setPlaceholder("Enter your access token");
        tokenField.setWidth("300px");

        Button loginButton = new Button("Login", e -> authenticate(tokenField.getValue().trim()));
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.setWidth("300px");

        add(title, tokenField, loginButton);
    }

    private void authenticate(String tokenValue) {
        if (tokenValue.isEmpty()) {
            Notification.show("Please enter an access token", 3000, Notification.Position.MIDDLE);
            return;
        }

        if (tokenService.validateAndUseToken(tokenValue)) {
            UsernamePasswordAuthenticationToken auth =
                UsernamePasswordAuthenticationToken.authenticated(
                    "admin", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

            VaadinServletRequest vaadinRequest = (VaadinServletRequest) VaadinRequest.getCurrent();
            HttpSession session = vaadinRequest.getHttpServletRequest().getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            UI.getCurrent().navigate(DashboardView.class);
        } else {
            Notification.show("Invalid or expired token", 3000, Notification.Position.MIDDLE);
            tokenField.clear();
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null
                && context.getAuthentication().isAuthenticated()
                && !context.getAuthentication().getAuthorities().isEmpty()) {
            event.forwardTo(DashboardView.class);
        }
    }
}
