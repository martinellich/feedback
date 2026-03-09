package ch.martinelli.jug.feedback.service;

public class QrCodeGenerationException extends RuntimeException {

    public QrCodeGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
