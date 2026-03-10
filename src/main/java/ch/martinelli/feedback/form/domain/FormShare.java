package ch.martinelli.feedback.form.domain;

public record FormShare(Long id, Long formId, String sharedWithEmail) {

    public FormShare withId(Long id) {
        return new FormShare(id, formId, sharedWithEmail);
    }
}
