CREATE TABLE app_clients
(
    id BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime NOT NULL,
    modified_at datetime NOT NULL,
    app_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    client_id VARCHAR(255) NOT NULL,
    client_secret VARCHAR(255) NOT NULL,
    grant_type VARCHAR(255) NOT NULL,
    CONSTRAINT pk_app_clients PRIMARY KEY (id),
    CONSTRAINT uc_app_clients_client_id UNIQUE (client_id)
);

CREATE TABLE app_client_redirect_uris
(
    client_id VARCHAR(255) NOT NULL,
    redirect_uri VARCHAR(255),
    CONSTRAINT fk_app_client_redirect_uris_client_id FOREIGN KEY (client_id) REFERENCES app_clients (client_id)
);

CREATE TABLE app_client_scopes
(
    client_id VARCHAR(255) NOT NULL,
    scope VARCHAR(255),
    CONSTRAINT fk_app_client_scopes_client_id FOREIGN KEY (client_id) REFERENCES app_clients (client_id)
);