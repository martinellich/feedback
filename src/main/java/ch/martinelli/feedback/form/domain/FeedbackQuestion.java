package ch.martinelli.feedback.form.domain;

public record FeedbackQuestion(Long id, Long formId, String questionText, QuestionType questionType,
                                Integer orderIndex) {

    public FeedbackQuestion withId(Long id) {
        return new FeedbackQuestion(id, formId, questionText, questionType, orderIndex);
    }

    public FeedbackQuestion withFormId(Long formId) {
        return new FeedbackQuestion(id, formId, questionText, questionType, orderIndex);
    }
}
