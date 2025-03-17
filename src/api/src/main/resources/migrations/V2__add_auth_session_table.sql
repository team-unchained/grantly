CREATE TABLE auth_session
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at  datetime     NOT NULL,
    modified_at datetime     NOT NULL,
    token       VARCHAR(255) NOT NULL,
    user_agent  VARCHAR(100) NULL,
    ip          VARCHAR(45) NULL,
    expires_at  datetime     NOT NULL,
    user_id     BIGINT       NOT NULL,
    CONSTRAINT pk_auth_session PRIMARY KEY (id)
);

ALTER TABLE auth_session
    ADD CONSTRAINT uq_user_id_token UNIQUE (user_id, token);

ALTER TABLE auth_session
    ADD CONSTRAINT FK_AUTH_SESSION_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);
