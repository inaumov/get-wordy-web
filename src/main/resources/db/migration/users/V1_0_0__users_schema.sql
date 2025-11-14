CREATE TABLE IF NOT EXISTS users
(
    username VARCHAR(50)  NOT NULL,
    password VARCHAR(500) NOT NULL,
    enabled  BOOLEAN      NOT NULL,
    PRIMARY KEY (username)
);

CREATE TABLE IF NOT EXISTS user_profiles
(
    username   VARCHAR(50) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    email      VARCHAR(60) NOT NULL UNIQUE,
    FOREIGN KEY (username) REFERENCES users (username)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS authorities
(
    username  VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    FOREIGN KEY (username) REFERENCES users (username)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS ix_auth_username ON authorities (username, authority);

CREATE TABLE IF NOT EXISTS groups
(
    id         BIGINT PRIMARY KEY,
    group_name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS group_members
(
    group_id BIGINT,
    username VARCHAR(50),
    FOREIGN KEY (group_id) REFERENCES groups (id),
    FOREIGN KEY (username) REFERENCES users (username)
);

CREATE TABLE IF NOT EXISTS group_authorities
(
    group_id  BIGINT,
    authority VARCHAR(50),
    FOREIGN KEY (group_id) REFERENCES groups (id)
);

INSERT INTO groups (id, group_name)
VALUES (11, 'individual_users');
INSERT INTO groups (id, group_name)
VALUES (12, 'learners');
INSERT INTO groups (id, group_name)
VALUES (13, 'teachers');
