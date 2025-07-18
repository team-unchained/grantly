CREATE TABLE user
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_at    datetime              NOT NULL,
    modified_at   datetime              NOT NULL,
    email         VARCHAR(100)          NOT NULL,
    password      VARCHAR(255)          NULL,
    name          VARCHAR(255)          NOT NULL,
    last_login_at datetime              NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

ALTER TABLE user
    ADD CONSTRAINT uc_user_email UNIQUE (email);
