package ch.martinelli.feedback.response.ui;

import ch.martinelli.feedback.form.domain.FeedbackForm;
import ch.martinelli.feedback.form.domain.FormService;
import ch.martinelli.feedback.form.domain.FormStatus;
import ch.martinelli.feedback.form.domain.QuestionType;
import ch.martinelli.feedback.response.domain.FeedbackAnswer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Route("form")
@AnonymousAllowed
public class PublicFormView extends VerticalLayout implements HasUrlParameter<String>, HasDynamicTitle {

    private final transient FormService formService;
    private final Map<Long, RadioButtonGroup<Integer>> ratingGroups = new HashMap<>();
    private final Map<Long, TextArea> textAreas = new HashMap<>();
    private transient FeedbackForm currentForm;

    public PublicFormView(FormService formService) {
        this.formService = formService;
        setPadding(true);
        setMaxWidth("800px");
        getStyle().set("margin", "0 auto");
    }

    @Override
    public String getPageTitle() {
        return getTranslation("form.page-title");
    }

    @Override
    public void setParameter(BeforeEvent event, String token) {
        var optForm = formService.getFormByPublicToken(token);

        if (optForm.isEmpty()) {
            add(new H2(getTranslation("form.not-found")),
                    new Paragraph(getTranslation("form.not-found.message")));
            return;
        }

        var form = optForm.get();

        if (form.status() != FormStatus.PUBLIC) {
            add(new H2(getTranslation("form.not-available")),
                    new Paragraph(getTranslation("form.not-available.message")));
            return;
        }

        currentForm = form;
        buildForm(form);
    }

    private void buildForm(FeedbackForm form) {
        var title = new H2(form.title());
        add(title);

        if (form.speakerName() != null && !form.speakerName().isEmpty()) {
            add(new Paragraph(getTranslation("form.speaker", form.speakerName())));
        }
        if (form.eventDate() != null) {
            add(new Paragraph(getTranslation("form.date", form.eventDate().toString())));
        }
        if (form.location() != null && !form.location().isEmpty()) {
            add(new Paragraph(getTranslation("form.location", form.location())));
        }

        add(new Paragraph(getTranslation("form.rating.instructions")));

        for (var question : form.questions()) {
            add(new H3(question.orderIndex() + ". " + question.questionText()));

            if (question.questionType() == QuestionType.RATING) {
                var ratingGroup = new RadioButtonGroup<Integer>();
                ratingGroup.addClassName("rating-group");
                ratingGroup.setItems(1, 2, 3, 4, 5);
                ratingGroup.setItemLabelGenerator(i -> getTranslation("form.rating." + i));
                add(ratingGroup);
                ratingGroups.put(question.id(), ratingGroup);
            } else {
                var textArea = new TextArea();
                textArea.setPlaceholder(getTranslation("form.text.placeholder"));
                textArea.setWidthFull();
                textArea.setMinHeight("100px");
                add(textArea);
                textAreas.put(question.id(), textArea);
            }
        }

        var submitButton = new Button(getTranslation("form.submit"), e -> submitFeedback());
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        submitButton.setWidthFull();
        add(submitButton);
    }

    private void submitFeedback() {
        var answers = new ArrayList<FeedbackAnswer>();

        for (var question : currentForm.questions()) {
            Integer ratingValue = null;
            String textValue = null;

            if (question.questionType() == QuestionType.RATING) {
                var group = ratingGroups.get(question.id());
                if (group != null && group.getValue() != null) {
                    ratingValue = group.getValue();
                }
            } else {
                var textArea = textAreas.get(question.id());
                if (textArea != null && !textArea.getValue().trim().isEmpty()) {
                    textValue = textArea.getValue().trim();
                }
            }

            answers.add(new FeedbackAnswer(null, null, question.id(), ratingValue, textValue));
        }

        formService.submitResponse(currentForm.id(), answers);

        removeAll();
        add(new H2(getTranslation("form.thank-you")),
                new Paragraph(getTranslation("form.thank-you.message")));
    }
}
