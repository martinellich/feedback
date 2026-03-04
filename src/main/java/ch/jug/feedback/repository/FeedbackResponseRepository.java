package ch.jug.feedback.repository;

import ch.jug.feedback.model.FeedbackForm;
import ch.jug.feedback.model.FeedbackResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackResponseRepository extends JpaRepository<FeedbackResponse, Long> {

    List<FeedbackResponse> findByForm(FeedbackForm form);

    long countByForm(FeedbackForm form);
}
