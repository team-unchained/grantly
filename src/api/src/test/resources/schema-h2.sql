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

CREATE TABLE auth_session
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at  datetime              NOT NULL,
    modified_at datetime              NOT NULL,
    token       VARCHAR(50)           NOT NULL,
    device_id   VARCHAR(50)           NOT NULL,
    user_agent  VARCHAR(255)          NULL,
    ip          VARCHAR(45)           NULL,
    expires_at  datetime              NOT NULL,
    user_id     BIGINT                NULL,
    CONSTRAINT pk_auth_session PRIMARY KEY (id)
);

ALTER TABLE auth_session
    ADD CONSTRAINT uc_auth_session_token UNIQUE (token);

ALTER TABLE auth_session
    ADD CONSTRAINT FK_AUTH_SESSION_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

CREATE TABLE token
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at  datetime              NOT NULL,
    modified_at datetime              NOT NULL,
    token       VARCHAR(50)           NOT NULL,
    expires_at  datetime              NOT NULL,
    type        INT                   NOT NULL,
    payload     TEXT                  NOT NULL,
    is_active   BOOLEAN               NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_token PRIMARY KEY (id)
);

ALTER TABLE token
    ADD CONSTRAINT uc_token_token UNIQUE (token);
