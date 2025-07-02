CREATE TABLE IF NOT EXISTS member
(
    id            BIGINT auto_increment NOT NULL,
    created_at    DATETIME NOT NULL,
    modified_at   DATETIME NOT NULL,
    email         VARCHAR(100) NOT NULL,
    password      VARCHAR(255) NULL,
    name          VARCHAR(255) NOT NULL,
    last_login_at DATETIME NULL,
    CONSTRAINT pk_member PRIMARY KEY (id)
    );

ALTER TABLE member
    ADD CONSTRAINT uc_member_email UNIQUE (email);

CREATE TABLE IF NOT EXISTS auth_session
(
    id           BIGINT auto_increment NOT NULL,
    created_at   DATETIME NOT NULL,
    modified_at  DATETIME NOT NULL,
    token        VARCHAR(50) NOT NULL,
    device_id    VARCHAR(50) NOT NULL,
    user_agent   VARCHAR(255) NULL,
    ip           VARCHAR(45) NULL,
    expires_at   DATETIME NOT NULL,
    subject_id   BIGINT NULL,
    subject_type SMALLINT NULL,
    CONSTRAINT pk_auth_session PRIMARY KEY (id)
    );

ALTER TABLE auth_session
    ADD CONSTRAINT uc_auth_session_token UNIQUE (token);

CREATE TABLE IF NOT EXISTS token
(
    id          BIGINT auto_increment NOT NULL,
    created_at  DATETIME NOT NULL,
    modified_at DATETIME NOT NULL,
    token       VARCHAR(50) NOT NULL,
    expires_at  DATETIME NOT NULL,
    type        INT NOT NULL,
    payload     TEXT NOT NULL,
    is_active   BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT pk_token PRIMARY KEY (id)
    );

ALTER TABLE token
    ADD CONSTRAINT uc_token_token UNIQUE (token);

CREATE TABLE IF NOT EXISTS application
(
    id            BIGINT auto_increment NOT NULL,
    created_at    DATETIME NOT NULL,
    modified_at   DATETIME NOT NULL,
    slug          VARCHAR(50) NOT NULL,
    name          VARCHAR(50) NOT NULL,
    image_url     VARCHAR(255) NULL,
    `description` VARCHAR(512) NULL,
    owner_id      BIGINT NOT NULL,
    is_active     BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT pk_service PRIMARY KEY (id)
    );

ALTER TABLE application
    ADD CONSTRAINT uc_application_slug UNIQUE (slug);

ALTER TABLE application
    ADD CONSTRAINT fk_application_on_owner FOREIGN KEY (owner_id) REFERENCES
        member (id);
