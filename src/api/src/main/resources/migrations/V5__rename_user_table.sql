ALTER TABLE user RENAME TO member;

ALTER TABLE auth_session RENAME COLUMN user_id TO member_id;
