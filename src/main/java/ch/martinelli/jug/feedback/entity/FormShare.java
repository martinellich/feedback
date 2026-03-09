package ch.martinelli.jug.feedback.entity;

public record FormShare(Long id, Long formId, String sharedWithEmail) {

    public FormShare withId(Long id) {
        return new FormShare(id, formId, sharedWithEmail);
    }
}
