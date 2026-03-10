package ch.martinelli.feedback.response.ui;

import ch.martinelli.feedback.form.domain.FeedbackForm;
import ch.martinelli.feedback.form.domain.FormService;
import ch.martinelli.feedback.form.domain.QuestionType;
import ch.martinelli.feedback.form.ui.DashboardView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("results")
@PermitAll
public class ResultsView extends VerticalLayout implements HasUrlParameter<Long>, HasDynamicTitle {

    private final transient FormService formService;

    public ResultsView(FormService formService) {
        this.formService = formService;
        setSizeFull();
        setPadding(true);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("results.page-title");
    }

    @Override
    public void setParameter(BeforeEvent event, Long formId) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!formService.hasAccess(formId, email)) {
            event.forwardTo(DashboardView.class);
            return;
        }
        formService.getFormById(formId).ifPresent(this::buildView);
    }

    private void buildView(FeedbackForm form) {
        removeAll();

        var backButton = new Button(getTranslation("results.back"),
            e -> UI.getCurrent().navigate(DashboardView.class));

        var title = new H2(getTranslation("results.title", form.title()));
        add(backButton, title);

        if (form.speakerName() != null && !form.speakerName().isEmpty()) {
            add(new Span(getTranslation("results.speaker", form.speakerName())));
        }

        var responseCount = formService.getResponseCount(form.id());
        add(new Paragraph(getTranslation("results.total-responses", responseCount)));

        if (responseCount == 0) {
            add(new Paragraph(getTranslation("results.no-responses")));
            return;
        }

        for (var question : form.questions()) {
            add(new H3(question.orderIndex() + ". " + question.questionText()));

            if (question.questionType() == QuestionType.RATING) {
                addRatingResult(question.id());
            } else {
                addTextResults(question.id());
            }
        }
    }

    private void addRatingResult(Long questionId) {
        var avg = formService.getAverageRating(questionId);
        if (avg != null) {
            add(new Paragraph(getTranslation("results.average-rating", String.format("%.2f", avg))));
        }
    }

    private void addTextResults(Long questionId) {
        var textAnswers = formService.getTextAnswers(questionId);
        for (var answer : textAnswers) {
            if (answer.textValue() != null && !answer.textValue().trim().isEmpty()) {
                var p = new Paragraph("\u2022 " + answer.textValue());
                p.getStyle().set("margin-left", "20px");
                add(p);
            }
        }
    }
}
