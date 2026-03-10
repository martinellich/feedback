package ch.martinelli.feedback.form.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record FormTemplate(Long id, String name, String ownerEmail, LocalDateTime createdAt,
                            List<TemplateQuestion> questions) {

    public FormTemplate(String name, String ownerEmail) {
        this(null, name, ownerEmail, LocalDateTime.now(), new ArrayList<>());
    }

    public FormTemplate withId(Long id) {
        return new FormTemplate(id, name, ownerEmail, createdAt, questions);
    }

    public FormTemplate withQuestions(List<TemplateQuestion> questions) {
        return new FormTemplate(id, name, ownerEmail, createdAt, questions);
    }
}
