package ch.martinelli.jug.feedback.repository;

import ch.martinelli.jug.feedback.entity.FormShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormShareRepository extends JpaRepository<FormShare, Long> {
    List<FormShare> findByFormId(Long formId);
    List<FormShare> findBySharedWithEmail(String email);
    void deleteByFormIdAndSharedWithEmail(Long formId, String email);
    boolean existsByFormIdAndSharedWithEmail(Long formId, String email);
}
