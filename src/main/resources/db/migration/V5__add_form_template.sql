CREATE SEQUENCE form_template_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE form_template
(
    id          BIGINT       NOT NULL DEFAULT nextval('form_template_seq') PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    owner_email VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL
);

CREATE SEQUENCE template_question_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE template_question
(
    id            BIGINT       NOT NULL DEFAULT nextval('template_question_seq') PRIMARY KEY,
    template_id   BIGINT       NOT NULL REFERENCES form_template (id) ON DELETE CASCADE,
    question_text VARCHAR(500) NOT NULL,
    question_type VARCHAR(10)  NOT NULL,
    order_index   INTEGER      NOT NULL
);
