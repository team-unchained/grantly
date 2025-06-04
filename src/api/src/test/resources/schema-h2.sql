CREATE TABLE IF NOT EXISTS member
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_at    datetime              NOT NULL,
    modified_at   datetime              NOT NULL,
    email         VARCHAR(100)          NOT NULL,
    password      VARCHAR(255)          NULL,
    name          VARCHAR(255)          NOT NULL,
    last_login_at datetime              NULL,
    CONSTRAINT pk_member PRIMARY KEY (id)
);

ALTER TABLE member
    ADD CONSTRAINT uc_member_email UNIQUE (email);

CREATE TABLE IF NOT EXISTS auth_session
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at  datetime              NOT NULL,
    modified_at datetime              NOT NULL,
    token       VARCHAR(50)           NOT NULL,
    device_id   VARCHAR(50)           NOT NULL,
    user_agent  VARCHAR(255)          NULL,
    ip          VARCHAR(45)           NULL,
    expires_at  datetime              NOT NULL,
    member_id     BIGINT                NULL,
    CONSTRAINT pk_auth_session PRIMARY KEY (id)
);

ALTER TABLE auth_session
    ADD CONSTRAINT uc_auth_session_token UNIQUE (token);

ALTER TABLE auth_session
    ADD CONSTRAINT FK_AUTH_SESSION_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);

CREATE TABLE IF NOT EXISTS token
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

CREATE TABLE IF NOT EXISTS service
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_at    datetime              NOT NULL,
    modified_at   datetime              NOT NULL,
    slug          VARCHAR(50)           NOT NULL,
    name          VARCHAR(50)           NOT NULL,
    image_url     VARCHAR(255)          NULL,
    `description` VARCHAR(512)          NULL,
    owner_id      BIGINT                NOT NULL,
    CONSTRAINT pk_service PRIMARY KEY (id)
);

ALTER TABLE service
    ADD CONSTRAINT uc_service_slug UNIQUE (slug);

ALTER TABLE service
    ADD CONSTRAINT FK_SERVICE_ON_OWNER FOREIGN KEY (owner_id) REFERENCES member (id);
