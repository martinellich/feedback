package ch.martinelli.feedback.usecases;

import ch.martinelli.feedback.KaribuTest;
import ch.martinelli.feedback.UseCase;
import ch.martinelli.feedback.form.domain.*;
import ch.martinelli.feedback.form.ui.DashboardView;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static com.github.mvysny.kaributesting.v10.GridKt._size;
import static com.github.mvysny.kaributesting.v10.LocatorJ.*;
import static com.github.mvysny.kaributesting.v10.NotificationsKt.expectNotifications;
import static org.assertj.core.api.Assertions.assertThat;

class UC13CreateFormFromTemplateTest extends KaribuTest {

    private static final String OWNER_EMAIL = "uc13-template@example.com";

    @Autowired
    private FormService formService;

    @Autowired
    private FormTemplateRepository formTemplateRepository;

    private FormTemplate savedTemplate;

    @BeforeEach
    void createTemplate() {
        formTemplateRepository.deleteByOwnerEmail(OWNER_EMAIL);
        // Clean up any leftover forms from previous test runs
        formService.getFormsForUser(OWNER_EMAIL).forEach(f -> formService.deleteForm(f.id()));

        // Create a form with questions and save it as a template
        var form = formService.createForm("Source Form", "Speaker", LocalDate.now(), "Location", OWNER_EMAIL);
        var questions = List.of(
                new FeedbackQuestion(null, form.id(), "How was the talk?", QuestionType.RATING, 1),
                new FeedbackQuestion(null, form.id(), "Any comments?", QuestionType.TEXT, 2)
        );
        formService.saveForm(form.withQuestions(questions));
        savedTemplate = formService.saveFormAsTemplate(form.id(), "My Template");

        // Delete the source form so it doesn't interfere with grid assertions
        formService.deleteForm(form.id());

        login(OWNER_EMAIL, List.of("USER"));
    }

    @Test
    @UseCase(id = "UC-13")
    void create_from_template_button_opens_dialog() {
        UI.getCurrent().navigate(DashboardView.class);

        _click(_get(Button.class, spec -> spec.withText("Create from Template")));

        var dialog = _get(Dialog.class);
        assertThat(dialog.isOpened()).isTrue();
        assertThat(_get(ComboBox.class, spec -> spec.withLabel("Template")).isVisible()).isTrue();
        assertThat(_get(TextField.class, spec -> spec.withLabel("Form Title")).isVisible()).isTrue();
        assertThat(_get(TextField.class, spec -> spec.withLabel("Speaker Name")).isVisible()).isTrue();
        assertThat(_get(DatePicker.class, spec -> spec.withLabel("Date")).isVisible()).isTrue();
        assertThat(_get(TextField.class, spec -> spec.withLabel("Location")).isVisible()).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Test
    @UseCase(id = "UC-13", businessRules = {"BR-027", "BR-028"})
    void create_form_from_template_copies_questions_as_draft() {
        UI.getCurrent().navigate(DashboardView.class);

        _click(_get(Button.class, spec -> spec.withText("Create from Template")));

        _setValue(_get(ComboBox.class, spec -> spec.withLabel("Template")), savedTemplate);
        _setValue(_get(TextField.class, spec -> spec.withLabel("Form Title")), "New Presentation");
        _setValue(_get(TextField.class, spec -> spec.withLabel("Speaker Name")), "Jane Doe");
        _setValue(_get(DatePicker.class, spec -> spec.withLabel("Date")), LocalDate.of(2026, 6, 15));
        _setValue(_get(TextField.class, spec -> spec.withLabel("Location")), "Bern");

        _click(_get(Button.class, spec -> spec.withText("Create")));

        expectNotifications("Form created successfully");

        // Verify form appears in grid
        var grid = _get(Grid.class);
        assertThat(_size(grid)).isEqualTo(1);

        // Verify form was created with correct data
        var forms = formService.getFormsForUser(OWNER_EMAIL);
        var createdForm = forms.stream()
                .filter(f -> "New Presentation".equals(f.title()))
                .findFirst()
                .orElseThrow();

        assertThat(createdForm.status()).isEqualTo(FormStatus.DRAFT);
        assertThat(createdForm.speakerName()).isEqualTo("Jane Doe");
        assertThat(createdForm.eventDate()).isEqualTo(LocalDate.of(2026, 6, 15));
        assertThat(createdForm.location()).isEqualTo("Bern");
        assertThat(createdForm.publicToken()).isNotBlank();

        // Verify questions were copied from template
        assertThat(createdForm.questions()).hasSize(2);
        assertThat(createdForm.questions().get(0).questionText()).isEqualTo("How was the talk?");
        assertThat(createdForm.questions().get(0).questionType()).isEqualTo(QuestionType.RATING);
        assertThat(createdForm.questions().get(0).orderIndex()).isEqualTo(1);
        assertThat(createdForm.questions().get(1).questionText()).isEqualTo("Any comments?");
        assertThat(createdForm.questions().get(1).questionType()).isEqualTo(QuestionType.TEXT);
        assertThat(createdForm.questions().get(1).orderIndex()).isEqualTo(2);
    }

    @Test
    @UseCase(id = "UC-13", scenario = "3a")
    void create_from_template_without_title_shows_validation_error() {
        UI.getCurrent().navigate(DashboardView.class);

        _click(_get(Button.class, spec -> spec.withText("Create from Template")));

        _setValue(_get(ComboBox.class, spec -> spec.withLabel("Template")), savedTemplate);
        // Leave title empty

        _click(_get(Button.class, spec -> spec.withText("Create")));

        // Dialog should still be open
        var dialog = _get(Dialog.class);
        assertThat(dialog.isOpened()).isTrue();
        assertThat(_get(TextField.class, spec -> spec.withLabel("Form Title")).isInvalid()).isTrue();

        // No form should be created
        var forms = formService.getFormsForUser(OWNER_EMAIL);
        assertThat(forms).isEmpty();
    }

    @Test
    @UseCase(id = "UC-13", scenario = "3a")
    void create_from_template_without_selecting_template_shows_validation_error() {
        UI.getCurrent().navigate(DashboardView.class);

        _click(_get(Button.class, spec -> spec.withText("Create from Template")));

        _setValue(_get(TextField.class, spec -> spec.withLabel("Form Title")), "Some Title");
        // Don't select a template

        _click(_get(Button.class, spec -> spec.withText("Create")));

        // Dialog should still be open
        var dialog = _get(Dialog.class);
        assertThat(dialog.isOpened()).isTrue();

        // No form should be created
        var forms = formService.getFormsForUser(OWNER_EMAIL);
        assertThat(forms).isEmpty();
    }

    @Test
    @UseCase(id = "UC-13", scenario = "3b")
    void cancel_dialog_does_not_create_form() {
        UI.getCurrent().navigate(DashboardView.class);

        _click(_get(Button.class, spec -> spec.withText("Create from Template")));

        _click(_get(Button.class, spec -> spec.withText("Cancel")));

        var forms = formService.getFormsForUser(OWNER_EMAIL);
        assertThat(forms).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    @UseCase(id = "UC-13", businessRules = "BR-027")
    void created_form_is_independent_of_template() {
        UI.getCurrent().navigate(DashboardView.class);

        _click(_get(Button.class, spec -> spec.withText("Create from Template")));

        _setValue(_get(ComboBox.class, spec -> spec.withLabel("Template")), savedTemplate);
        _setValue(_get(TextField.class, spec -> spec.withLabel("Form Title")), "Independent Form");

        _click(_get(Button.class, spec -> spec.withText("Create")));

        expectNotifications("Form created successfully");

        // Delete the template
        formTemplateRepository.deleteByOwnerEmail(OWNER_EMAIL);

        // Form should still exist with its questions
        var forms = formService.getFormsForUser(OWNER_EMAIL);
        var form = forms.stream()
                .filter(f -> "Independent Form".equals(f.title()))
                .findFirst()
                .orElseThrow();
        assertThat(form.questions()).hasSize(2);
    }
}
