package ch.martinelli.feedback.form.domain;

import org.jooq.DSLContext;
import org.jooq.Records;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static ch.martinelli.feedback.db.tables.FormTemplate.FORM_TEMPLATE;
import static ch.martinelli.feedback.db.tables.TemplateQuestion.TEMPLATE_QUESTION;

@Repository
public class FormTemplateRepository {

    private final DSLContext dsl;

    public FormTemplateRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional
    public FormTemplate save(FormTemplate template) {
        var id = dsl.insertInto(FORM_TEMPLATE)
                .set(FORM_TEMPLATE.NAME, template.name())
                .set(FORM_TEMPLATE.OWNER_EMAIL, template.ownerEmail())
                .set(FORM_TEMPLATE.CREATED_AT, template.createdAt())
                .returning(FORM_TEMPLATE.ID)
                .fetchOne(FORM_TEMPLATE.ID);

        var savedQuestions = new ArrayList<TemplateQuestion>();
        for (var question : template.questions()) {
            var q = question.withTemplateId(id);
            var qId = dsl.insertInto(TEMPLATE_QUESTION)
                    .set(TEMPLATE_QUESTION.TEMPLATE_ID, q.templateId())
                    .set(TEMPLATE_QUESTION.QUESTION_TEXT, q.questionText())
                    .set(TEMPLATE_QUESTION.QUESTION_TYPE, q.questionType())
                    .set(TEMPLATE_QUESTION.ORDER_INDEX, q.orderIndex())
                    .returning(TEMPLATE_QUESTION.ID)
                    .fetchOne(TEMPLATE_QUESTION.ID);
            savedQuestions.add(q.withId(qId));
        }

        return template.withId(id).withQuestions(savedQuestions);
    }

    @Transactional
    public void deleteById(Long id) {
        dsl.deleteFrom(FORM_TEMPLATE)
                .where(FORM_TEMPLATE.ID.eq(id))
                .execute();
    }

    @Transactional
    public void deleteByOwnerEmail(String email) {
        dsl.deleteFrom(FORM_TEMPLATE)
                .where(FORM_TEMPLATE.OWNER_EMAIL.eq(email))
                .execute();
    }

    @Transactional
    public void updateName(Long id, String name) {
        dsl.update(FORM_TEMPLATE)
                .set(FORM_TEMPLATE.NAME, name)
                .where(FORM_TEMPLATE.ID.eq(id))
                .execute();
    }

    public List<FormTemplate> findByOwnerEmail(String email) {
        return dsl.select(FORM_TEMPLATE.ID, FORM_TEMPLATE.NAME, FORM_TEMPLATE.OWNER_EMAIL, FORM_TEMPLATE.CREATED_AT)
                .from(FORM_TEMPLATE)
                .where(FORM_TEMPLATE.OWNER_EMAIL.eq(email))
                .orderBy(FORM_TEMPLATE.CREATED_AT.desc())
                .fetch(Records.mapping((id, name, ownerEmail, createdAt) ->
                        new FormTemplate(id, name, ownerEmail, createdAt, new ArrayList<>())));
    }

    public List<FormTemplate> findByOwnerEmailWithQuestions(String email) {
        var templates = findByOwnerEmail(email);
        if (templates.isEmpty()) {
            return templates;
        }

        var templateIds = templates.stream().map(FormTemplate::id).toList();
        var questionsByTemplateId = dsl.select(TEMPLATE_QUESTION.ID, TEMPLATE_QUESTION.TEMPLATE_ID,
                        TEMPLATE_QUESTION.QUESTION_TEXT, TEMPLATE_QUESTION.QUESTION_TYPE, TEMPLATE_QUESTION.ORDER_INDEX)
                .from(TEMPLATE_QUESTION)
                .where(TEMPLATE_QUESTION.TEMPLATE_ID.in(templateIds))
                .orderBy(TEMPLATE_QUESTION.ORDER_INDEX)
                .fetch(Records.mapping(TemplateQuestion::new))
                .stream()
                .collect(java.util.stream.Collectors.groupingBy(TemplateQuestion::templateId));

        return templates.stream()
                .map(t -> t.withQuestions(questionsByTemplateId.getOrDefault(t.id(), List.of())))
                .toList();
    }
}
