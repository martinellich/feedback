package ch.jug.feedback.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "feedback_answer")
public class FeedbackAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", nullable = false)
    private FeedbackResponse response;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private FeedbackQuestion question;

    @Column(name = "rating_value")
    private Integer ratingValue;

    @Column(name = "text_value", length = 2000)
    private String textValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FeedbackResponse getResponse() {
        return response;
    }

    public void setResponse(FeedbackResponse response) {
        this.response = response;
    }

    public FeedbackQuestion getQuestion() {
        return question;
    }

    public void setQuestion(FeedbackQuestion question) {
        this.question = question;
    }

    public Integer getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Integer ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }
}
