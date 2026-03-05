package ch.martinelli.jug.feedback.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "form_share", uniqueConstraints = @UniqueConstraint(columnNames = {"form_id", "shared_with_email"}))
public class FormShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false)
    private FeedbackForm form;

    @Column(name = "shared_with_email", nullable = false)
    private String sharedWithEmail;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public FeedbackForm getForm() { return form; }
    public void setForm(FeedbackForm form) { this.form = form; }

    public String getSharedWithEmail() { return sharedWithEmail; }
    public void setSharedWithEmail(String sharedWithEmail) { this.sharedWithEmail = sharedWithEmail; }
}
