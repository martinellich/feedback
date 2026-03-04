package ch.jug.feedback.ui;

import ch.jug.feedback.model.*;
import ch.jug.feedback.service.FormService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Route(value = "results", layout = MainLayout.class)
@PageTitle("Ergebnisse - JUG Feedback")
public class ResultsView extends VerticalLayout implements BeforeEnterObserver, HasUrlParameter<Long> {

    private final FormService formService;

    public ResultsView(FormService formService) {
        this.formService = formService;
        setSizeFull();
        setPadding(true);
    }

    @Override
    public void setParameter(BeforeEvent event, Long formId) {
        if (formId == null) {
            UI.getCurrent().navigate(DashboardView.class);
            return;
        }
        loadResults(formId);
    }

    private void loadResults(Long formId) {
        removeAll();

        FeedbackForm form = formService.getById(formId);
        List<FeedbackResponse> responses = formService.getResponsesForForm(formId);

        Button backButton = new Button("Zurück zum Dashboard", VaadinIcon.ARROW_LEFT.create());
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> UI.getCurrent().navigate(DashboardView.class));

        H2 title = new H2("Ergebnisse: " + form.getTitle());
        title.getStyle().set("margin-top", "0.5rem");

        HorizontalLayout stats = new HorizontalLayout();
        stats.addClassName("stats-bar");

        Div responsesCount = createStatCard("Antworten", String.valueOf(responses.size()));
        Div statusCard = createStatCard("Status", form.getStatus().name());
        stats.add(responsesCount, statusCard);

        add(backButton, title, stats);

        if (responses.isEmpty()) {
            Paragraph noData = new Paragraph("Noch keine Antworten vorhanden.");
            noData.getStyle().set("color", "var(--lumo-secondary-text-color)");
            add(noData);
            return;
        }

        H3 ratingsTitle = new H3("Bewertungen (Durchschnitt)");
        add(ratingsTitle);

        List<FeedbackQuestion> ratingQuestions = form.getQuestions().stream()
                .filter(q -> q.getQuestionType() == QuestionType.RATING)
                .collect(Collectors.toList());

        if (!ratingQuestions.isEmpty()) {
            Grid<FeedbackQuestion> ratingsGrid = new Grid<>();
            ratingsGrid.setHeight("auto");
            ratingsGrid.addColumn(FeedbackQuestion::getQuestionText).setHeader("Frage").setFlexGrow(1);
            ratingsGrid.addColumn(question -> {
                OptionalDouble avg = responses.stream()
                        .flatMap(r -> r.getAnswers().stream())
                        .filter(a -> a.getQuestion().getId().equals(question.getId()))
                        .filter(a -> a.getRatingValue() != null)
                        .mapToInt(FeedbackAnswer::getRatingValue)
                        .average();
                return avg.isPresent() ? String.format("%.1f / 5", avg.getAsDouble()) : "Keine Antworten";
            }).setHeader("Durchschnitt").setAutoWidth(true);
            ratingsGrid.addColumn(question -> {
                long count = responses.stream()
                        .flatMap(r -> r.getAnswers().stream())
                        .filter(a -> a.getQuestion().getId().equals(question.getId()))
                        .filter(a -> a.getRatingValue() != null)
                        .count();
                return count + " Antworten";
            }).setHeader("Anzahl").setAutoWidth(true);
            ratingsGrid.setItems(ratingQuestions);
            add(ratingsGrid);
        }

        List<FeedbackQuestion> textQuestions = form.getQuestions().stream()
                .filter(q -> q.getQuestionType() == QuestionType.TEXT)
                .collect(Collectors.toList());

        if (!textQuestions.isEmpty()) {
            H3 commentsTitle = new H3("Kommentare");
            add(commentsTitle);

            for (FeedbackQuestion question : textQuestions) {
                List<String> texts = responses.stream()
                        .flatMap(r -> r.getAnswers().stream())
                        .filter(a -> a.getQuestion().getId().equals(question.getId()))
                        .filter(a -> a.getTextValue() != null && !a.getTextValue().isBlank())
                        .map(FeedbackAnswer::getTextValue)
                        .collect(Collectors.toList());

                if (!texts.isEmpty()) {
                    H4 questionText = new H4(question.getQuestionText());
                    add(questionText);

                    for (String text : texts) {
                        Paragraph comment = new Paragraph(text);
                        comment.getStyle()
                                .set("background", "var(--lumo-contrast-5pct)")
                                .set("padding", "0.75rem")
                                .set("border-radius", "4px")
                                .set("margin", "0.25rem 0");
                        add(comment);
                    }
                }
            }
        }

        H3 responsesTitle = new H3("Alle Antworten");
        add(responsesTitle);

        Grid<FeedbackResponse> responsesGrid = new Grid<>();
        responsesGrid.setHeight("300px");
        responsesGrid.addColumn(r -> r.getSubmittedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
                .setHeader("Eingereicht am").setAutoWidth(true);
        responsesGrid.addColumn(r -> r.getAnswers().size() + " Antworten")
                .setHeader("Antworten").setAutoWidth(true);
        responsesGrid.setItems(responses);
        add(responsesGrid);
    }

    private Div createStatCard(String label, String value) {
        Div card = new Div();
        card.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("padding", "1rem 1.5rem")
                .set("border-radius", "8px")
                .set("text-align", "center")
                .set("min-width", "150px");

        Paragraph labelEl = new Paragraph(label);
        labelEl.getStyle().set("color", "var(--lumo-secondary-text-color)")
                .set("margin", "0").set("font-size", "0.875rem");

        Paragraph valueEl = new Paragraph(value);
        valueEl.getStyle().set("font-size", "2rem").set("font-weight", "bold")
                .set("margin", "0.25rem 0 0 0");

        card.add(labelEl, valueEl);
        return card;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Boolean authenticated = (Boolean) VaadinSession.getCurrent().getAttribute("authenticated");
        if (!Boolean.TRUE.equals(authenticated)) {
            event.forwardTo(LoginView.class);
        }
    }
}
