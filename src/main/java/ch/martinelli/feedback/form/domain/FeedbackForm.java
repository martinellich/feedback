package ch.martinelli.feedback.form.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record FeedbackForm(Long id, String title, String speakerName, LocalDate eventDate, String location,
                            FormStatus status, String publicToken, String ownerEmail, LocalDateTime createdAt,
                            List<FeedbackQuestion> questions) {

    public FeedbackForm(String title, String speakerName, LocalDate eventDate, String location, String ownerEmail) {
        this(null, title, speakerName, eventDate, location, FormStatus.DRAFT,
                UUID.randomUUID().toString(), ownerEmail, LocalDateTime.now(), new ArrayList<>());
    }

    public FeedbackForm withId(Long id) {
        return new FeedbackForm(id, title, speakerName, eventDate, location, status, publicToken, ownerEmail,
                createdAt, questions);
    }

    public FeedbackForm withStatus(FormStatus status) {
        return new FeedbackForm(id, title, speakerName, eventDate, location, status, publicToken, ownerEmail,
                createdAt, questions);
    }

    public FeedbackForm withQuestions(List<FeedbackQuestion> questions) {
        return new FeedbackForm(id, title, speakerName, eventDate, location, status, publicToken, ownerEmail,
                createdAt, questions);
    }

    public FeedbackForm withDetails(String title, String speakerName, LocalDate eventDate, String location) {
        return new FeedbackForm(id, title, speakerName, eventDate, location, status, publicToken, ownerEmail,
                createdAt, questions);
    }
}
