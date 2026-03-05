ALTER TABLE feedback_form ADD COLUMN event_date DATE;
ALTER TABLE feedback_form ADD COLUMN location VARCHAR(255);
ALTER TABLE feedback_form DROP COLUMN topic;
