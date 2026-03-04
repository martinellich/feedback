package ch.jug.feedback.repository;

import ch.jug.feedback.model.FeedbackForm;
import ch.jug.feedback.model.FormStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackFormRepository extends JpaRepository<FeedbackForm, Long> {

    Optional<FeedbackForm> findByPublicToken(String publicToken);

    List<FeedbackForm> findByStatus(FormStatus status);

    List<FeedbackForm> findAllByOrderByCreatedAtDesc();
}
