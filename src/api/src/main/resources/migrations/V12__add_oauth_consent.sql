CREATE TABLE oauth_consent
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_at    datetime              NOT NULL,
    modified_at   datetime              NOT NULL,
    user_id       BIGINT                NULL,
    app_client_id BIGINT                NULL,
    consented_at  datetime              NOT NULL,
    CONSTRAINT pk_oauth_consent PRIMARY KEY (id)
);

CREATE TABLE oauth_consent_granted_scope
(
    consent_id    BIGINT       NOT NULL,
    granted_scope VARCHAR(255) NULL
);

ALTER TABLE oauth_consent
    ADD CONSTRAINT FK_OAUTH_CONSENT_ON_APPCLIENT FOREIGN KEY (app_client_id) REFERENCES app_client (id);

ALTER TABLE oauth_consent
    ADD CONSTRAINT FK_OAUTH_CONSENT_ON_USER FOREIGN KEY (user_id) REFERENCES member (id);

ALTER TABLE oauth_consent_granted_scope
    ADD CONSTRAINT fk_oauth_consent_granted_scope_on_o_auth_consent_jpa_entity FOREIGN KEY (consent_id) REFERENCES oauth_consent (id);
