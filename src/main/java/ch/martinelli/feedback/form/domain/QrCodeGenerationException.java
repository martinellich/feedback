package ch.martinelli.feedback.form.domain;

public class QrCodeGenerationException extends RuntimeException {

    public QrCodeGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
