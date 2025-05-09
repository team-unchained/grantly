CREATE TABLE token
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at  datetime              NOT NULL,
    modified_at datetime              NOT NULL,
    token       VARCHAR(50)           NOT NULL,
    expires_at  datetime              NOT NULL,
    type        INT                   NOT NULL,
    payload     JSON                  NULL,
    is_active   BOOLEAN               NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_token PRIMARY KEY (id)
);

ALTER TABLE token
    ADD CONSTRAINT uc_token_token UNIQUE (token);
