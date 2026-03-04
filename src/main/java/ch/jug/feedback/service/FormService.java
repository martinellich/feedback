package ch.jug.feedback.service;

import ch.jug.feedback.model.*;
import ch.jug.feedback.repository.FeedbackFormRepository;
import ch.jug.feedback.repository.FeedbackResponseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FormService {

    private final FeedbackFormRepository formRepository;
    private final FeedbackResponseRepository responseRepository;

    public FormService(FeedbackFormRepository formRepository,
                       FeedbackResponseRepository responseRepository) {
        this.formRepository = formRepository;
        this.responseRepository = responseRepository;
    }

    public FeedbackForm createFormFromTemplate(String title, String speakerName, String talkTitle) {
        FeedbackForm form = new FeedbackForm();
        form.setTitle(title);
        form.setSpeakerName(speakerName);
        form.setTalkTitle(talkTitle);
        form.setStatus(FormStatus.DRAFT);

        addDefaultQuestions(form);

        return formRepository.save(form);
    }

    private void addDefaultQuestions(FeedbackForm form) {
        int order = 0;

        // Content questions
        form.getQuestions().add(createRatingQuestion(form, "Fachlicher Inhalt",
                "Wie bewerten Sie den fachlichen Inhalt des Vortrags?", order++));
        form.getQuestions().add(createRatingQuestion(form, "Aktualität",
                "Wie aktuell und relevant war das Thema?", order++));
        form.getQuestions().add(createRatingQuestion(form, "Tiefe des Themas",
                "War die Tiefe der Behandlung des Themas angemessen?", order++));
        form.getQuestions().add(createRatingQuestion(form, "Praxisbezug",
                "Wie gut wurden praktische Beispiele eingesetzt?", order++));

        // Presentation questions
        form.getQuestions().add(createRatingQuestion(form, "Präsentation",
                "Wie bewerten Sie die Qualität der Präsentation (Folien, Struktur)?", order++));
        form.getQuestions().add(createRatingQuestion(form, "Verständlichkeit",
                "War der Vortrag gut verständlich?", order++));
        form.getQuestions().add(createRatingQuestion(form, "Tempo",
                "War das Tempo des Vortrags angemessen?", order++));

        // Speaker questions
        form.getQuestions().add(createRatingQuestion(form, "Kompetenz des Referenten",
                "Wie schätzen Sie die fachliche Kompetenz des Referenten ein?", order++));
        form.getQuestions().add(createRatingQuestion(form, "Vortragsstil",
                "Wie bewerten Sie den Vortragsstil des Referenten?", order++));

        // Overall
        form.getQuestions().add(createRatingQuestion(form, "Gesamteindruck",
                "Wie ist Ihr Gesamteindruck des Vortrags?", order++));

        // Open text questions
        form.getQuestions().add(createTextQuestion(form, "Was hat Ihnen besonders gut gefallen?",
                "Bitte teilen Sie uns mit, was Sie am Vortrag besonders positiv fanden.", order++));
        form.getQuestions().add(createTextQuestion(form, "Was sollte verbessert werden?",
                "Bitte teilen Sie uns Verbesserungsvorschläge mit.", order++));
        form.getQuestions().add(createTextQuestion(form, "Sonstige Anmerkungen",
                "Haben Sie weitere Kommentare oder Anregungen?", order));
    }

    private FeedbackQuestion createRatingQuestion(FeedbackForm form, String text, String description, int order) {
        FeedbackQuestion question = new FeedbackQuestion();
        question.setForm(form);
        question.setQuestionText(text);
        question.setQuestionDescription(description);
        question.setQuestionType(QuestionType.RATING);
        question.setSortOrder(order);
        question.setMaxRating(5);
        question.setRequired(true);
        return question;
    }

    private FeedbackQuestion createTextQuestion(FeedbackForm form, String text, String description, int order) {
        FeedbackQuestion question = new FeedbackQuestion();
        question.setForm(form);
        question.setQuestionText(text);
        question.setQuestionDescription(description);
        question.setQuestionType(QuestionType.TEXT);
        question.setSortOrder(order);
        question.setRequired(false);
        return question;
    }

    public FeedbackForm save(FeedbackForm form) {
        return formRepository.save(form);
    }

    public FeedbackForm publish(Long formId) {
        FeedbackForm form = getById(formId);
        form.setStatus(FormStatus.PUBLIC);
        form.setPublishedAt(LocalDateTime.now());
        return formRepository.save(form);
    }

    public FeedbackForm close(Long formId) {
        FeedbackForm form = getById(formId);
        form.setStatus(FormStatus.CLOSED);
        form.setClosedAt(LocalDateTime.now());
        return formRepository.save(form);
    }

    public FeedbackForm reopen(Long formId) {
        FeedbackForm form = getById(formId);
        form.setStatus(FormStatus.PUBLIC);
        form.setClosedAt(null);
        return formRepository.save(form);
    }

    @Transactional(readOnly = true)
    public FeedbackForm getById(Long id) {
        return formRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Form not found: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<FeedbackForm> findByPublicToken(String token) {
        return formRepository.findByPublicToken(token);
    }

    @Transactional(readOnly = true)
    public List<FeedbackForm> getAllForms() {
        return formRepository.findAllByOrderByCreatedAtDesc();
    }

    public FeedbackResponse submitResponse(Long formId, List<FeedbackAnswer> answers) {
        FeedbackForm form = getById(formId);

        if (form.getStatus() != FormStatus.PUBLIC) {
            throw new IllegalStateException("Form is not accepting responses");
        }

        FeedbackResponse response = new FeedbackResponse();
        response.setForm(form);

        for (FeedbackAnswer answer : answers) {
            answer.setResponse(response);
            response.getAnswers().add(answer);
        }

        return responseRepository.save(response);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getResponsesForForm(Long formId) {
        FeedbackForm form = getById(formId);
        return responseRepository.findByForm(form);
    }

    public void deleteForm(Long formId) {
        formRepository.deleteById(formId);
    }
}
