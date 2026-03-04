package ch.jug.feedback.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("")
@AnonymousAllowed
public class RootRedirectView extends VerticalLayout implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Boolean authenticated = (Boolean) VaadinSession.getCurrent().getAttribute("authenticated");
        if (Boolean.TRUE.equals(authenticated)) {
            event.forwardTo(DashboardView.class);
        } else {
            event.forwardTo(LoginView.class);
        }
    }
}
