ALTER TABLE auth_session
    DROP FOREIGN KEY FK_AUTH_SESSION_ON_MEMBER;

ALTER TABLE auth_session
    DROP COLUMN member_id;

ALTER TABLE auth_session
    ADD COLUMN subject_id BIGINT NULL;

ALTER TABLE auth_session
    ADD COLUMN subject_type SMALLINT NULL;
