package ch.jug.feedback.ui;

import ch.jug.feedback.model.FeedbackForm;
import ch.jug.feedback.model.FeedbackQuestion;
import ch.jug.feedback.model.FormStatus;
import ch.jug.feedback.model.QuestionType;
import ch.jug.feedback.service.FormService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.List;

@Route(value = "form-editor", layout = MainLayout.class)
@PageTitle("Formular bearbeiten - JUG Feedback")
public class FormEditorView extends VerticalLayout implements BeforeEnterObserver, HasUrlParameter<Long> {

    private final FormService formService;

    private FeedbackForm currentForm;
    private boolean isNew = true;

    private TextField titleField;
    private TextField speakerNameField;
    private TextField talkTitleField;
    private DateTimePicker eventDatePicker;
    private Grid<FeedbackQuestion> questionGrid;

    public FormEditorView(FormService formService) {
        this.formService = formService;
        setSizeFull();
        setPadding(true);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long formId) {
        if (formId != null) {
            currentForm = formService.getById(formId);
            isNew = false;
        } else {
            currentForm = null;
            isNew = true;
        }
        buildUI();
    }

    private void buildUI() {
        removeAll();

        H2 title = new H2(isNew ? "Neues Formular erstellen" : "Formular bearbeiten");
        title.getStyle().set("margin-top", "0");

        VerticalLayout formSection = createFormSection();
        VerticalLayout questionsSection = createQuestionsSection();

        HorizontalLayout buttonBar = createButtonBar();

        add(title, formSection, questionsSection, buttonBar);
    }

    private VerticalLayout createFormSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);

        H3 sectionTitle = new H3("Vortrag Details");
        sectionTitle.getStyle().set("margin-bottom", "0");

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        titleField = new TextField("Formular Titel");
        titleField.setRequired(true);
        titleField.setPlaceholder("z.B. JUG Meeting März 2024");

        speakerNameField = new TextField("Referent");
        speakerNameField.setRequired(true);
        speakerNameField.setPlaceholder("Vor- und Nachname");

        talkTitleField = new TextField("Vortragstitel");
        talkTitleField.setPlaceholder("Titel des Vortrags");

        eventDatePicker = new DateTimePicker("Datum/Uhrzeit");

        if (currentForm != null) {
            titleField.setValue(currentForm.getTitle() != null ? currentForm.getTitle() : "");
            speakerNameField.setValue(currentForm.getSpeakerName() != null ? currentForm.getSpeakerName() : "");
            talkTitleField.setValue(currentForm.getTalkTitle() != null ? currentForm.getTalkTitle() : "");
            if (currentForm.getEventDate() != null) {
                eventDatePicker.setValue(currentForm.getEventDate());
            }
        }

        formLayout.add(titleField, speakerNameField, talkTitleField, eventDatePicker);
        section.add(sectionTitle, formLayout);
        return section;
    }

    private VerticalLayout createQuestionsSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);

        H3 sectionTitle = new H3("Fragen");
        sectionTitle.getStyle().set("margin-bottom", "0");

        boolean canEdit = currentForm == null || currentForm.getStatus() == FormStatus.DRAFT;

        HorizontalLayout questionToolbar = new HorizontalLayout();
        if (canEdit) {
            Button addRatingQuestion = new Button("Bewertungsfrage hinzufügen",
                    VaadinIcon.STAR.create());
            addRatingQuestion.addThemeVariants(ButtonVariant.LUMO_SMALL);
            addRatingQuestion.addClickListener(e -> addQuestion(QuestionType.RATING));

            Button addTextQuestion = new Button("Textfrage hinzufügen",
                    VaadinIcon.TEXT_INPUT.create());
            addTextQuestion.addThemeVariants(ButtonVariant.LUMO_SMALL);
            addTextQuestion.addClickListener(e -> addQuestion(QuestionType.TEXT));

            questionToolbar.add(addRatingQuestion, addTextQuestion);
        }

        questionGrid = new Grid<>(FeedbackQuestion.class, false);
        questionGrid.setHeight("300px");

        questionGrid.addColumn(FeedbackQuestion::getQuestionText)
                .setHeader("Frage").setFlexGrow(2);
        questionGrid.addColumn(q -> q.getQuestionType() == QuestionType.RATING ?
                "Bewertung (1-" + q.getMaxRating() + ")" : "Freitext")
                .setHeader("Typ").setAutoWidth(true);
        questionGrid.addColumn(q -> q.isRequired() ? "Ja" : "Nein")
                .setHeader("Pflicht").setAutoWidth(true);

        if (canEdit) {
            questionGrid.addComponentColumn(question -> {
                HorizontalLayout actions = new HorizontalLayout();
                actions.setSpacing(true);

                Button moveUpButton = new Button(VaadinIcon.ARROW_UP.create());
                moveUpButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
                moveUpButton.addClickListener(e -> moveQuestion(question, -1));

                Button moveDownButton = new Button(VaadinIcon.ARROW_DOWN.create());
                moveDownButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
                moveDownButton.addClickListener(e -> moveQuestion(question, 1));

                Button deleteButton = new Button(VaadinIcon.TRASH.create());
                deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR,
                        ButtonVariant.LUMO_TERTIARY);
                deleteButton.addClickListener(e -> removeQuestion(question));

                actions.add(moveUpButton, moveDownButton, deleteButton);
                return actions;
            }).setHeader("Aktionen").setAutoWidth(true);
        }

        if (currentForm != null) {
            questionGrid.setItems(new ArrayList<>(currentForm.getQuestions()));
        }

        section.add(sectionTitle, questionToolbar, questionGrid);
        return section;
    }

    private void addQuestion(QuestionType type) {
        if (currentForm == null) {
            currentForm = new FeedbackForm();
            currentForm.setStatus(FormStatus.DRAFT);
        }

        FeedbackQuestion question = new FeedbackQuestion();
        question.setForm(currentForm);
        question.setQuestionType(type);
        question.setRequired(type == QuestionType.RATING);
        question.setMaxRating(5);
        question.setSortOrder(currentForm.getQuestions().size());

        if (type == QuestionType.RATING) {
            question.setQuestionText("Neue Bewertungsfrage");
        } else {
            question.setQuestionText("Neue Textfrage");
        }

        currentForm.getQuestions().add(question);
        questionGrid.setItems(new ArrayList<>(currentForm.getQuestions()));
    }

    private void moveQuestion(FeedbackQuestion question, int direction) {
        List<FeedbackQuestion> questions = currentForm.getQuestions();
        int index = questions.indexOf(question);
        int newIndex = index + direction;

        if (newIndex >= 0 && newIndex < questions.size()) {
            questions.remove(index);
            questions.add(newIndex, question);
            for (int i = 0; i < questions.size(); i++) {
                questions.get(i).setSortOrder(i);
            }
            questionGrid.setItems(new ArrayList<>(questions));
        }
    }

    private void removeQuestion(FeedbackQuestion question) {
        currentForm.getQuestions().remove(question);
        questionGrid.setItems(new ArrayList<>(currentForm.getQuestions()));
    }

    private HorizontalLayout createButtonBar() {
        HorizontalLayout buttonBar = new HorizontalLayout();
        buttonBar.setPadding(false);

        Button saveButton = new Button("Speichern", VaadinIcon.CHECK.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> save());

        Button cancelButton = new Button("Abbrechen");
        cancelButton.addClickListener(e -> UI.getCurrent().navigate(DashboardView.class));

        buttonBar.add(saveButton, cancelButton);
        return buttonBar;
    }

    private void save() {
        if (titleField.getValue().isBlank()) {
            Notification.show("Bitte geben Sie einen Titel ein", 3000,
                    Notification.Position.MIDDLE);
            return;
        }
        if (speakerNameField.getValue().isBlank()) {
            Notification.show("Bitte geben Sie einen Referenten ein", 3000,
                    Notification.Position.MIDDLE);
            return;
        }

        if (isNew) {
            currentForm = formService.createFormFromTemplate(
                    titleField.getValue(),
                    speakerNameField.getValue(),
                    talkTitleField.getValue()
            );
        } else {
            currentForm.setTitle(titleField.getValue());
            currentForm.setSpeakerName(speakerNameField.getValue());
            currentForm.setTalkTitle(talkTitleField.getValue());
            currentForm.setEventDate(eventDatePicker.getValue());
            formService.save(currentForm);
        }

        Notification notification = Notification.show(
                isNew ? "Formular erstellt" : "Formular gespeichert",
                3000, Notification.Position.BOTTOM_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        UI.getCurrent().navigate(DashboardView.class);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Boolean authenticated = (Boolean) VaadinSession.getCurrent().getAttribute("authenticated");
        if (!Boolean.TRUE.equals(authenticated)) {
            event.forwardTo(LoginView.class);
        }
    }
}
