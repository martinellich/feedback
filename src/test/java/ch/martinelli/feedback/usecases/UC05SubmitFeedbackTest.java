package ch.martinelli.feedback.usecases;

import ch.martinelli.feedback.KaribuTest;
import ch.martinelli.feedback.UseCase;
import ch.martinelli.feedback.form.domain.FeedbackQuestion;
import ch.martinelli.feedback.form.domain.FeedbackQuestionRepository;
import ch.martinelli.feedback.form.domain.FormService;
import ch.martinelli.feedback.form.domain.QuestionType;
import ch.martinelli.feedback.response.ui.PublicFormView;
import com.github.mvysny.fakeservlet.FakeRequest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static com.github.mvysny.kaributesting.v10.LocatorJ.*;
import static org.assertj.core.api.Assertions.assertThat;

class UC05SubmitFeedbackTest extends KaribuTest {

    private static final String OWNER_EMAIL = "uc05-feedback@example.com";

    @Autowired
    private FormService formService;

    @Autowired
    private FeedbackQuestionRepository questionRepository;

    private String publicToken;
    private Long formId;

    @BeforeEach
    void createPublicForm() {
        var form = formService.createForm("Feedback Test", "Test Speaker", LocalDate.of(2026, 3, 15), "Zurich", OWNER_EMAIL);
        formId = form.id();
        publicToken = form.publicToken();

        // Add questions: 2 RATING + 1 TEXT
        questionRepository.save(new FeedbackQuestion(null, formId, "Content quality", QuestionType.RATING, 1));
        questionRepository.save(new FeedbackQuestion(null, formId, "Speaker competence", QuestionType.RATING, 2));
        questionRepository.save(new FeedbackQuestion(null, formId, "Additional comments", QuestionType.TEXT, 3));

        formService.publishForm(form.id());
    }

    @Test
    @UseCase(id = "UC-05", businessRules = "BR-007")
    void public_form_displays_title_and_questions() {
        UI.getCurrent().navigate(PublicFormView.class, publicToken);

        assertThat(_get(H2.class, spec -> spec.withText("Feedback Test")).isVisible()).isTrue();

        // Should have 2 rating groups and 1 text area
        var ratingGroups = _find(RadioButtonGroup.class);
        var textAreas = _find(TextArea.class);
        assertThat(ratingGroups).hasSize(2);
        assertThat(textAreas).hasSize(1);

        assertThat(_get(Button.class, spec -> spec.withText("Submit Feedback")).isVisible()).isTrue();
    }

    @Test
    @UseCase(id = "UC-05", businessRules = {"BR-007", "BR-008", "BR-009"})
    void submit_feedback_shows_thank_you_page() {
        UI.getCurrent().navigate(PublicFormView.class, publicToken);

        // Fill in some ratings
        var ratingGroups = _find(RadioButtonGroup.class);
        for (var group : ratingGroups) {
            @SuppressWarnings("unchecked")
            RadioButtonGroup<Integer> rg = (RadioButtonGroup<Integer>) group;
            _setValue(rg, 4);
        }

        // Fill in a text answer
        var textAreas = _find(TextArea.class);
        _setValue(textAreas.getFirst(), "Great presentation!");

        _click(_get(Button.class, spec -> spec.withText("Submit Feedback")));

        // Should show thank you message
        assertThat(_get(H2.class, spec -> spec.withText("Thank you!")).isVisible()).isTrue();

        // Verify response was saved
        assertThat(formService.getResponseCount(formId)).isEqualTo(1);
    }

    @Test
    @UseCase(id = "UC-05", scenario = "A1")
    void form_not_found_shows_error() {
        UI.getCurrent().navigate(PublicFormView.class, "nonexistent-token");

        assertThat(_get(H2.class, spec -> spec.withText("Form not found")).isVisible()).isTrue();
    }

    @Test
    @UseCase(id = "UC-05", scenario = "A2")
    void closed_form_shows_not_available() {
        formService.closeForm(formId);

        UI.getCurrent().navigate(PublicFormView.class, publicToken);

        assertThat(_get(H2.class, spec -> spec.withText("Form not available")).isVisible()).isTrue();
    }

    @Test
    @UseCase(id = "UC-05", scenario = "A2")
    void draft_form_shows_not_available() {
        // Create a draft form (not published)
        var draftForm = formService.createForm("Draft Form", "Speaker", LocalDate.now(), "Location", OWNER_EMAIL);

        UI.getCurrent().navigate(PublicFormView.class, draftForm.publicToken());

        assertThat(_get(H2.class, spec -> spec.withText("Form not available")).isVisible()).isTrue();
    }

    @Test
    @UseCase(id = "UC-05", businessRules = "BR-008")
    void empty_text_answer_is_not_saved() {
        UI.getCurrent().navigate(PublicFormView.class, publicToken);

        // Fill in ratings but leave text area empty
        var ratingGroups = _find(RadioButtonGroup.class);
        for (var group : ratingGroups) {
            @SuppressWarnings("unchecked")
            RadioButtonGroup<Integer> rg = (RadioButtonGroup<Integer>) group;
            _setValue(rg, 3);
        }

        // Leave text area empty (default)
        _click(_get(Button.class, spec -> spec.withText("Submit Feedback")));

        // Verify response was saved
        assertThat(formService.getResponseCount(formId)).isEqualTo(1);

        // Verify text answers for the TEXT question are empty
        var form = formService.getFormById(formId).orElseThrow();
        var textQuestion = form.questions().stream()
                .filter(q -> q.questionType() == QuestionType.TEXT)
                .findFirst().orElseThrow();
        var textAnswers = formService.getTextAnswers(textQuestion.id());
        var nonEmptyAnswers = textAnswers.stream()
                .filter(a -> a.textValue() != null && !a.textValue().trim().isEmpty())
                .toList();
        assertThat(nonEmptyAnswers).isEmpty();
    }

    @Test
    @UseCase(id = "UC-05", scenario = "A3", businessRules = "BR-011")
    void already_submitted_shows_message_when_cookie_present() {
        // Simulate a previously submitted cookie on the request
        var fakeRequest = (FakeRequest) VaadinServletRequest.getCurrent().getRequest();
        fakeRequest.addCookie(new Cookie("feedback_submitted_" + formId, "true"));

        UI.getCurrent().navigate(PublicFormView.class, publicToken);

        assertThat(_get(H2.class, spec -> spec.withText("Already submitted")).isVisible()).isTrue();
    }
}
