package ch.martinelli.jug.feedback;

import com.github.mvysny.fakeservlet.FakeRequest;
import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import com.github.mvysny.kaributesting.v10.spring.MockSpringServlet;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import kotlin.jvm.functions.Function0;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Locale;

@Import(TestContainersConfiguration.class)
@SpringBootTest
public abstract class KaribuTest {

    private static Routes routes;

    @Autowired
    protected ApplicationContext ctx;

    @BeforeAll
    public static void discoverRoutes() {
        routes = new Routes().autoDiscoverViews("ch.martinelli.jug.feedback");
        Locale.setDefault(Locale.ENGLISH);
    }

    @BeforeEach
    public void setup() {
        final Function0<UI> uiFactory = UI::new;
        var servlet = new MockSpringServlet(routes, ctx, uiFactory);
        MockVaadin.setup(uiFactory, servlet);
    }

    @AfterEach
    public void tearDown() {
        logout();
        MockVaadin.tearDown();
    }

    protected void login(String user, final List<String> roles) {
        var auth = UsernamePasswordAuthenticationToken.authenticated(
                user, null, roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        var sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);

        // however, you also need to make sure that ViewAccessChecker works properly.
        // that requires a correct MockRequest userPrincipal and MockRequest isUserInRole
        var request = (FakeRequest) VaadinServletRequest.getCurrent().getRequest();
        request.setUserPrincipalInt(auth);
        request.setUserInRole((_, role) -> roles.contains(role));
    }

    protected void logout() {
        try {
            SecurityContextHolder.getContext().setAuthentication(null);
            if (VaadinServletRequest.getCurrent() != null) {
                var request = (FakeRequest) VaadinServletRequest.getCurrent().getRequest();
                request.setUserPrincipalInt(null);
                request.setUserInRole((_, _) -> false);
            }
        } catch (IllegalStateException _) {
            // Ignore
        }
    }

}
