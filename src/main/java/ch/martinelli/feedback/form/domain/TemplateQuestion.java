package ch.martinelli.feedback.form.domain;

public record TemplateQuestion(Long id, Long templateId, String questionText, QuestionType questionType,
                                Integer orderIndex) {

    public TemplateQuestion withId(Long id) {
        return new TemplateQuestion(id, templateId, questionText, questionType, orderIndex);
    }

    public TemplateQuestion withTemplateId(Long templateId) {
        return new TemplateQuestion(id, templateId, questionText, questionType, orderIndex);
    }
}
