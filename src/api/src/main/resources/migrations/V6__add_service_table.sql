CREATE TABLE service
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_at    datetime              NOT NULL,
    modified_at   datetime              NOT NULL,
    slug          VARCHAR(50)           NOT NULL,
    name          VARCHAR(50)           NOT NULL,
    image_url     VARCHAR(255)          NULL,
    `description` VARCHAR(512)          NULL,
    owner_id      BIGINT                NOT NULL,
    is_active     BOOLEAN               NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_service PRIMARY KEY (id)
);

ALTER TABLE service
    ADD CONSTRAINT uc_service_slug UNIQUE (slug);

ALTER TABLE service
    ADD CONSTRAINT FK_SERVICE_ON_OWNER FOREIGN KEY (owner_id) REFERENCES member (id);
