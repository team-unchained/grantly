CREATE TABLE IF NOT EXISTS member
(
    id            BIGINT auto_increment NOT NULL,
    created_at    DATETIME              NOT NULL,
    modified_at   DATETIME              NOT NULL,
    email         VARCHAR(100)          NOT NULL,
    password      VARCHAR(255)          NULL,
    name          VARCHAR(255)          NOT NULL,
    last_login_at DATETIME              NULL,
    CONSTRAINT pk_member PRIMARY KEY (id)
);

ALTER TABLE member
    ADD CONSTRAINT IF NOT EXISTS uc_member_email UNIQUE (email);

CREATE TABLE IF NOT EXISTS auth_session
(
    id           BIGINT auto_increment NOT NULL,
    created_at   DATETIME              NOT NULL,
    modified_at  DATETIME              NOT NULL,
    token        VARCHAR(50)           NOT NULL,
    device_id    VARCHAR(50)           NOT NULL,
    user_agent   VARCHAR(255)          NULL,
    ip           VARCHAR(45)           NULL,
    expires_at   DATETIME              NOT NULL,
    subject_id   BIGINT                NULL,
    subject_type SMALLINT              NULL,
    CONSTRAINT pk_auth_session PRIMARY KEY (id)
);

ALTER TABLE auth_session
    ADD CONSTRAINT IF NOT EXISTS uc_auth_session_token UNIQUE (token);

CREATE TABLE IF NOT EXISTS token
(
    id          BIGINT auto_increment NOT NULL,
    created_at  DATETIME              NOT NULL,
    modified_at DATETIME              NOT NULL,
    token       VARCHAR(50)           NOT NULL,
    expires_at  DATETIME              NOT NULL,
    type        INT                   NOT NULL,
    payload     TEXT                  NOT NULL,
    is_active   BOOLEAN               NOT NULL DEFAULT true,
    CONSTRAINT pk_token PRIMARY KEY (id)
);

ALTER TABLE token
    ADD CONSTRAINT IF NOT EXISTS uc_token_token UNIQUE (token);

CREATE TABLE IF NOT EXISTS application
(
    id            BIGINT auto_increment NOT NULL,
    created_at    DATETIME              NOT NULL,
    modified_at   DATETIME              NOT NULL,
    slug          VARCHAR(50)           NOT NULL,
    name          VARCHAR(50)           NOT NULL,
    image_url     VARCHAR(255)          NULL,
    `description` VARCHAR(512)          NULL,
    owner_id      BIGINT                NOT NULL,
    is_active     BOOLEAN               NOT NULL DEFAULT true,
    CONSTRAINT pk_service PRIMARY KEY (id)
);

ALTER TABLE application
    ADD CONSTRAINT IF NOT EXISTS uc_application_slug UNIQUE (slug);

ALTER TABLE application
    DROP CONSTRAINT IF EXISTS fk_application_on_owner;
ALTER TABLE application
    ADD CONSTRAINT fk_application_on_owner FOREIGN KEY (owner_id) REFERENCES member (id);

CREATE TABLE app_client
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_at    datetime              NOT NULL,
    modified_at   datetime              NOT NULL,
    app_id        BIGINT                NOT NULL,
    title         VARCHAR(255)          NOT NULL,
    client_id     VARCHAR(255)          NOT NULL,
    client_secret VARCHAR(255)          NOT NULL,
    grant_type    VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_app_clients PRIMARY KEY (id),
    CONSTRAINT uc_app_clients_client_id UNIQUE (client_id)
);

CREATE TABLE app_client_redirect_uri
(
    client_id    VARCHAR(255) NOT NULL,
    redirect_uri VARCHAR(255),
    CONSTRAINT fk_app_client_redirect_uris_client_id FOREIGN KEY (client_id) REFERENCES app_client (client_id)
);

CREATE TABLE app_client_scope
(
    client_id VARCHAR(255) NOT NULL,
    scope     VARCHAR(255),
    CONSTRAINT fk_app_client_scopes_client_id FOREIGN KEY (client_id) REFERENCES app_client (client_id)
);

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
