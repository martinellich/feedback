package ch.martinelli.feedback.response.domain;

public record FeedbackAnswer(Long id, Long responseId, Long questionId, Integer ratingValue, String textValue) {

    public FeedbackAnswer withId(Long id) {
        return new FeedbackAnswer(id, responseId, questionId, ratingValue, textValue);
    }

    public FeedbackAnswer withResponseId(Long responseId) {
        return new FeedbackAnswer(id, responseId, questionId, ratingValue, textValue);
    }
}
