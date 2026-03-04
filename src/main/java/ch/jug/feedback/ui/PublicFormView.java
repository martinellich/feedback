package ch.jug.feedback.ui;

import ch.jug.feedback.model.*;
import ch.jug.feedback.service.FormService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Route("form")
@PageTitle("Feedback - JUG")
@AnonymousAllowed
public class PublicFormView extends VerticalLayout implements HasUrlParameter<String> {

    private final FormService formService;

    private FeedbackForm currentForm;
    private final Map<Long, RadioButtonGroup<Integer>> ratingComponents = new HashMap<>();
    private final Map<Long, TextArea> textComponents = new HashMap<>();

    public PublicFormView(FormService formService) {
        this.formService = formService;
        setAlignItems(Alignment.CENTER);
        setPadding(true);
        setSpacing(false);
    }

    @Override
    public void setParameter(BeforeEvent event, String token) {
        if (token == null || token.isBlank()) {
            showError("Ungültiger Link");
            return;
        }

        Optional<FeedbackForm> formOpt = formService.findByPublicToken(token);
        if (formOpt.isEmpty()) {
            showError("Formular nicht gefunden");
            return;
        }

        currentForm = formOpt.get();

        if (currentForm.getStatus() != FormStatus.PUBLIC) {
            if (currentForm.getStatus() == FormStatus.DRAFT) {
                showError("Dieses Formular ist noch nicht veröffentlicht");
            } else {
                showError("Dieses Formular ist geschlossen und nimmt keine weiteren Antworten an");
            }
            return;
        }

        buildForm();
    }

    private void buildForm() {
        removeAll();

        VerticalLayout container = new VerticalLayout();
        container.setMaxWidth("700px");
        container.setWidthFull();
        container.setPadding(true);

        Div header = new Div();
        header.getStyle().set("background", "var(--lumo-primary-color)")
                .set("color", "white")
                .set("padding", "2rem")
                .set("border-radius", "8px 8px 0 0")
                .set("width", "100%");

        H1 title = new H1("Feedback Bewertungsbogen");
        title.getStyle().set("color", "white").set("margin", "0").set("font-size", "1.75rem");

        H2 eventTitle = new H2(currentForm.getTitle());
        eventTitle.getStyle().set("color", "rgba(255,255,255,0.9)").set("margin", "0.5rem 0 0 0")
                .set("font-size", "1.2rem").set("font-weight", "normal");

        Paragraph speakerInfo = new Paragraph("Referent: " + currentForm.getSpeakerName());
        speakerInfo.getStyle().set("color", "rgba(255,255,255,0.8)").set("margin", "0.25rem 0 0 0");

        if (currentForm.getTalkTitle() != null && !currentForm.getTalkTitle().isBlank()) {
            Paragraph talkInfo = new Paragraph("Vortrag: " + currentForm.getTalkTitle());
            talkInfo.getStyle().set("color", "rgba(255,255,255,0.8)").set("margin", "0.25rem 0 0 0");
            header.add(title, eventTitle, speakerInfo, talkInfo);
        } else {
            header.add(title, eventTitle, speakerInfo);
        }

        container.add(header);

        Paragraph instructions = new Paragraph(
                "Bitte bewerten Sie den Vortrag auf einer Skala von 1 (sehr schlecht) bis 5 (sehr gut). " +
                        "Ihre Antworten sind anonym.");
        instructions.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "0.9rem")
                .set("margin", "1rem 0");

        container.add(instructions);

        String currentSection = null;
        for (FeedbackQuestion question : currentForm.getQuestions()) {
            String section = getSectionName(question);
            if (section != null && !section.equals(currentSection)) {
                currentSection = section;
                H3 sectionTitle = new H3(section);
                sectionTitle.getStyle()
                        .set("border-bottom", "2px solid var(--lumo-primary-color)")
                        .set("padding-bottom", "0.5rem")
                        .set("color", "var(--lumo-primary-color)");
                container.add(sectionTitle);
            }
            container.add(createQuestionComponent(question));
        }

        Button submitButton = new Button("Bewertung abschicken", e -> submitForm());
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        submitButton.setWidthFull();
        submitButton.getStyle().set("margin-top", "1.5rem");

        container.add(submitButton);

        add(container);
    }

    private String getSectionName(FeedbackQuestion question) {
        String text = question.getQuestionText().toLowerCase();
        if (text.contains("inhalt") || text.contains("aktualität") || text.contains("tiefe") ||
                text.contains("praxisbezug")) {
            return "Inhalt";
        } else if (text.contains("präsentation") || text.contains("verständlichkeit") ||
                text.contains("tempo")) {
            return "Präsentation";
        } else if (text.contains("kompetenz") || text.contains("vortragsstil")) {
            return "Referent";
        } else if (text.contains("gesamteindruck")) {
            return "Gesamtbewertung";
        } else if (question.getQuestionType() == QuestionType.TEXT) {
            return "Kommentare";
        }
        return null;
    }

    private VerticalLayout createQuestionComponent(FeedbackQuestion question) {
        VerticalLayout questionLayout = new VerticalLayout();
        questionLayout.setPadding(true);
        questionLayout.setSpacing(false);
        questionLayout.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "8px")
                .set("margin-bottom", "0.5rem");

        Paragraph questionText = new Paragraph(question.getQuestionText() +
                (question.isRequired() ? " *" : ""));
        questionText.getStyle().set("font-weight", "bold").set("margin", "0 0 0.25rem 0");

        if (question.getQuestionDescription() != null && !question.getQuestionDescription().isBlank()) {
            Paragraph description = new Paragraph(question.getQuestionDescription());
            description.getStyle().set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "0.85rem").set("margin", "0 0 0.5rem 0");
            questionLayout.add(questionText, description);
        } else {
            questionLayout.add(questionText);
        }

        if (question.getQuestionType() == QuestionType.RATING) {
            RadioButtonGroup<Integer> ratingGroup = new RadioButtonGroup<>();
            ratingGroup.setItems(1, 2, 3, 4, 5);
            ratingGroup.setItemLabelGenerator(value -> {
                return switch (value) {
                    case 1 -> "1 - Sehr schlecht";
                    case 2 -> "2 - Schlecht";
                    case 3 -> "3 - Befriedigend";
                    case 4 -> "4 - Gut";
                    case 5 -> "5 - Sehr gut";
                    default -> String.valueOf(value);
                };
            });
            ratingGroup.addClassName("rating-group");

            HorizontalLayout ratingLayout = new HorizontalLayout();
            ratingLayout.add(ratingGroup);

            questionLayout.add(ratingLayout);
            ratingComponents.put(question.getId(), ratingGroup);
        } else {
            TextArea textArea = new TextArea();
            textArea.setPlaceholder("Ihr Kommentar...");
            textArea.setWidthFull();
            textArea.setMaxLength(2000);
            questionLayout.add(textArea);
            textComponents.put(question.getId(), textArea);
        }

        return questionLayout;
    }

    private void submitForm() {
        List<FeedbackAnswer> answers = new ArrayList<>();

        for (FeedbackQuestion question : currentForm.getQuestions()) {
            if (question.getQuestionType() == QuestionType.RATING) {
                RadioButtonGroup<Integer> ratingGroup = ratingComponents.get(question.getId());
                if (ratingGroup != null) {
                    if (question.isRequired() && ratingGroup.getValue() == null) {
                        Notification notification = Notification.show(
                                "Bitte beantworten Sie alle Pflichtfragen (markiert mit *)",
                                3000, Notification.Position.MIDDLE);
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        return;
                    }
                    if (ratingGroup.getValue() != null) {
                        FeedbackAnswer answer = new FeedbackAnswer();
                        answer.setQuestion(question);
                        answer.setRatingValue(ratingGroup.getValue());
                        answers.add(answer);
                    }
                }
            } else {
                TextArea textArea = textComponents.get(question.getId());
                if (textArea != null && !textArea.getValue().isBlank()) {
                    FeedbackAnswer answer = new FeedbackAnswer();
                    answer.setQuestion(question);
                    answer.setTextValue(textArea.getValue());
                    answers.add(answer);
                }
            }
        }

        try {
            formService.submitResponse(currentForm.getId(), answers);
            showThankYou();
        } catch (Exception e) {
            Notification notification = Notification.show(
                    "Fehler beim Speichern: " + e.getMessage(),
                    5000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void showThankYou() {
        removeAll();

        VerticalLayout container = new VerticalLayout();
        container.setAlignItems(Alignment.CENTER);
        container.setJustifyContentMode(JustifyContentMode.CENTER);
        container.setSizeFull();

        H1 thankYou = new H1("Vielen Dank!");
        thankYou.getStyle().set("color", "var(--lumo-primary-color)");

        Paragraph message = new Paragraph(
                "Ihr Feedback wurde erfolgreich gespeichert. Wir danken Ihnen für Ihre Zeit und Ihre " +
                        "wertvollen Rückmeldungen!");
        message.getStyle().set("text-align", "center").set("font-size", "1.1rem");

        container.add(thankYou, message);
        add(container);
    }

    private void showError(String message) {
        removeAll();

        VerticalLayout container = new VerticalLayout();
        container.setAlignItems(Alignment.CENTER);
        container.setJustifyContentMode(JustifyContentMode.CENTER);
        container.setSizeFull();

        H1 errorTitle = new H1("Oops!");
        errorTitle.getStyle().set("color", "var(--lumo-error-color)");

        Paragraph errorMessage = new Paragraph(message);
        errorMessage.getStyle().set("text-align", "center").set("font-size", "1.1rem")
                .set("color", "var(--lumo-secondary-text-color)");

        container.add(errorTitle, errorMessage);
        add(container);
    }
}
