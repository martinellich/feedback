package ch.martinelli.feedback.response.domain;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static ch.martinelli.feedback.db.tables.FeedbackResponse.FEEDBACK_RESPONSE;

@Repository
public class FeedbackResponseRepository {

    private final DSLContext dsl;

    public FeedbackResponseRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional
    public FeedbackResponse save(FeedbackResponse response) {
        if (response.id() == null) {
            var id = dsl.insertInto(FEEDBACK_RESPONSE)
                    .set(FEEDBACK_RESPONSE.FORM_ID, response.formId())
                    .set(FEEDBACK_RESPONSE.SUBMITTED_AT, response.submittedAt())
                    .returning(FEEDBACK_RESPONSE.ID)
                    .fetchOne(FEEDBACK_RESPONSE.ID);
            return response.withId(id);
        } else {
            dsl.update(FEEDBACK_RESPONSE)
                    .set(FEEDBACK_RESPONSE.FORM_ID, response.formId())
                    .set(FEEDBACK_RESPONSE.SUBMITTED_AT, response.submittedAt())
                    .where(FEEDBACK_RESPONSE.ID.eq(response.id()))
                    .execute();
            return response;
        }
    }

    public long countByFormId(Long formId) {
        return dsl.selectCount()
                .from(FEEDBACK_RESPONSE)
                .where(FEEDBACK_RESPONSE.FORM_ID.eq(formId))
                .fetchOptionalInto(long.class).orElse(0L);
    }
}
