package ch.martinelli.feedback.usecases;

import ch.martinelli.feedback.KaribuTest;
import ch.martinelli.feedback.UseCase;
import ch.martinelli.feedback.form.domain.*;
import ch.martinelli.feedback.form.ui.TemplatesView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static com.github.mvysny.kaributesting.v10.GridKt._getCellComponent;
import static com.github.mvysny.kaributesting.v10.GridKt._size;
import static com.github.mvysny.kaributesting.v10.LocatorJ.*;
import static com.github.mvysny.kaributesting.v10.NotificationsKt.expectNotifications;
import static org.assertj.core.api.Assertions.assertThat;

class UC14ManageTemplatesTest extends KaribuTest {

    private static final String OWNER_EMAIL = "uc14-templates@example.com";

    @Autowired
    private FormService formService;

    @Autowired
    private FormTemplateRepository formTemplateRepository;

    @BeforeEach
    void setupData() {
        formTemplateRepository.deleteByOwnerEmail(OWNER_EMAIL);
        login(OWNER_EMAIL, List.of("USER"));
    }

    private void createTemplate(String name) {
        var form = formService.createForm("Form for " + name, "Speaker", LocalDate.now(), "Location", OWNER_EMAIL);
        var questions = List.of(
                new FeedbackQuestion(null, form.id(), "Rating question?", QuestionType.RATING, 1),
                new FeedbackQuestion(null, form.id(), "Text question?", QuestionType.TEXT, 2)
        );
        formService.saveForm(form.withQuestions(questions));
        formService.saveFormAsTemplate(form.id(), name);
    }

    @SuppressWarnings("unchecked")
    private Button findActionButton(int row, String text) {
        Grid<FormTemplate> grid = _get(Grid.class);
        var actions = (HorizontalLayout) _getCellComponent(grid, row, "actions");
        return actions.getChildren()
                .filter(c -> c instanceof Button btn && text.equals(btn.getText()))
                .map(c -> (Button) c)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Button '" + text + "' not found in row " + row));
    }

    @Test
    @UseCase(id = "UC-14")
    void displays_template_list() {
        createTemplate("Template A");
        createTemplate("Template B");

        UI.getCurrent().navigate(TemplatesView.class);

        Grid<FormTemplate> grid = _get(Grid.class);
        assertThat(_size(grid)).isEqualTo(2);
    }

    @Test
    @UseCase(id = "UC-14", scenario = "3c")
    void displays_empty_state_when_no_templates() {
        UI.getCurrent().navigate(TemplatesView.class);

        var emptyMessage = _get(Span.class, spec -> spec.withText("You don't have any templates yet."));
        assertThat(emptyMessage.isVisible()).isTrue();
    }

    @Test
    @UseCase(id = "UC-14", scenario = "3a")
    void rename_template() {
        createTemplate("Old Name");

        UI.getCurrent().navigate(TemplatesView.class);

        _click(findActionButton(0, "Rename"));

        var dialog = _get(Dialog.class);
        assertThat(dialog.isOpened()).isTrue();

        var nameField = _get(TextField.class, spec -> spec.withLabel("Template Name"));
        assertThat(nameField.getValue()).isEqualTo("Old Name");

        _setValue(nameField, "New Name");
        _click(_get(Button.class, spec -> spec.withText("Save")));

        expectNotifications("Template renamed successfully");

        var templates = formTemplateRepository.findByOwnerEmail(OWNER_EMAIL);
        assertThat(templates).hasSize(1);
        assertThat(templates.getFirst().name()).isEqualTo("New Name");
    }

    @Test
    @UseCase(id = "UC-14", scenario = "3a-3a", businessRules = "BR-029")
    void rename_template_with_empty_name_shows_validation_error() {
        createTemplate("My Template");

        UI.getCurrent().navigate(TemplatesView.class);

        _click(findActionButton(0, "Rename"));

        var nameField = _get(TextField.class, spec -> spec.withLabel("Template Name"));
        _setValue(nameField, "");

        _click(_get(Button.class, spec -> spec.withText("Save")));

        var dialog = _get(Dialog.class);
        assertThat(dialog.isOpened()).isTrue();
        assertThat(nameField.isInvalid()).isTrue();

        // Name should remain unchanged
        var templates = formTemplateRepository.findByOwnerEmail(OWNER_EMAIL);
        assertThat(templates.getFirst().name()).isEqualTo("My Template");
    }

    @Test
    @UseCase(id = "UC-14", scenario = "3b", businessRules = {"BR-030", "BR-031"})
    void delete_template() {
        createTemplate("To Delete");

        UI.getCurrent().navigate(TemplatesView.class);

        _click(findActionButton(0, "Delete"));

        expectNotifications("Template deleted successfully");

        var templates = formTemplateRepository.findByOwnerEmail(OWNER_EMAIL);
        assertThat(templates).isEmpty();
    }

    @Test
    @UseCase(id = "UC-14", scenario = "3b", businessRules = "BR-030")
    void delete_template_does_not_affect_forms_created_from_it() {
        createTemplate("Shared Template");

        var templates = formTemplateRepository.findByOwnerEmailWithQuestions(OWNER_EMAIL);
        var template = templates.getFirst();
        formService.createFormFromTemplate(template, "Form from Template", "Speaker", LocalDate.now(), "Location", OWNER_EMAIL);

        UI.getCurrent().navigate(TemplatesView.class);

        _click(findActionButton(0, "Delete"));

        expectNotifications("Template deleted successfully");

        // Template is gone
        assertThat(formTemplateRepository.findByOwnerEmail(OWNER_EMAIL)).isEmpty();

        // Form still exists
        var forms = formService.getFormsForUser(OWNER_EMAIL);
        assertThat(forms).anyMatch(f -> f.title().equals("Form from Template"));
    }

    @Test
    @UseCase(id = "UC-14", scenario = "3a")
    void cancel_rename_does_not_change_name() {
        createTemplate("Original Name");

        UI.getCurrent().navigate(TemplatesView.class);

        _click(findActionButton(0, "Rename"));

        var nameField = _get(TextField.class, spec -> spec.withLabel("Template Name"));
        _setValue(nameField, "Changed Name");

        _click(_get(Button.class, spec -> spec.withText("Cancel")));

        var templates = formTemplateRepository.findByOwnerEmail(OWNER_EMAIL);
        assertThat(templates.getFirst().name()).isEqualTo("Original Name");
    }
}
