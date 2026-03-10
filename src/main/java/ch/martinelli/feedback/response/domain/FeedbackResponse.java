package ch.martinelli.feedback.response.domain;

import java.time.LocalDateTime;

public record FeedbackResponse(Long id, Long formId, LocalDateTime submittedAt) {

    public FeedbackResponse withId(Long id) {
        return new FeedbackResponse(id, formId, submittedAt);
    }
}
