ALTER TABLE feedback_question DROP CONSTRAINT IF EXISTS feedback_question_form_id_fkey;
ALTER TABLE feedback_question ADD CONSTRAINT feedback_question_form_id_fkey
    FOREIGN KEY (form_id) REFERENCES feedback_form(id) ON DELETE CASCADE;

ALTER TABLE feedback_response DROP CONSTRAINT IF EXISTS feedback_response_form_id_fkey;
ALTER TABLE feedback_response ADD CONSTRAINT feedback_response_form_id_fkey
    FOREIGN KEY (form_id) REFERENCES feedback_form(id) ON DELETE CASCADE;

ALTER TABLE feedback_answer DROP CONSTRAINT IF EXISTS feedback_answer_response_id_fkey;
ALTER TABLE feedback_answer ADD CONSTRAINT feedback_answer_response_id_fkey
    FOREIGN KEY (response_id) REFERENCES feedback_response(id) ON DELETE CASCADE;

ALTER TABLE feedback_answer DROP CONSTRAINT IF EXISTS feedback_answer_question_id_fkey;
ALTER TABLE feedback_answer ADD CONSTRAINT feedback_answer_question_id_fkey
    FOREIGN KEY (question_id) REFERENCES feedback_question(id) ON DELETE CASCADE;
