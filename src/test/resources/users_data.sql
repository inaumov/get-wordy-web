-- User ivan@test.com/pass
INSERT INTO users (username, password, enabled)
VALUES ('IvanDrago-test', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', true);

INSERT INTO user_profiles (username, first_name, last_name, email)
VALUES ('IvanDrago-test', 'Ivan', 'Drago', 'ivan@test.com');

INSERT INTO authorities (username, authority)
VALUES ('IvanDrago-test', 'ROLE_USER');

INSERT INTO group_members (group_id, username)
VALUES (3, 'IvanDrago-test');

-- User lilian@test.com/pass
INSERT INTO users (username, password, enabled)
VALUES ('LilianVoss-test', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', true);

INSERT INTO user_profiles (username, first_name, last_name, email)
VALUES ('LilianVoss-test', 'Lilian', 'Voss', 'lilian@test.com');

INSERT INTO authorities (username, authority)
VALUES ('LilianVoss-test', 'ROLE_USER');

INSERT INTO group_members (group_id, username)
VALUES (1, 'LilianVoss-test');

-- User leeroy@test.com/pass
INSERT INTO users (username, password, enabled)
VALUES ('LeeroyJenkins-test', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', true);

INSERT INTO user_profiles (username, first_name, last_name, email)
VALUES ('LeeroyJenkins-test', 'Leeroy', 'Jenkins', 'leeroy@test.com');

INSERT INTO authorities (username, authority)
VALUES ('LeeroyJenkins-test', 'ROLE_USER');

INSERT INTO group_members (group_id, username)
VALUES (1, 'LeeroyJenkins-test');
INSERT INTO group_members (group_id, username)
VALUES (2, 'LeeroyJenkins-test');

-- User helen@test.com/pass
INSERT INTO users (username, password, enabled)
VALUES ('HelenWebb-test', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', true);

INSERT INTO user_profiles (username, first_name, last_name, email)
VALUES ('HelenWebb-test', 'Helen', 'Webb', 'helen@test.com');

INSERT INTO authorities (username, authority)
VALUES ('HelenWebb-test', 'ROLE_USER');

INSERT INTO group_members (group_id, username)
VALUES (1, 'HelenWebb-test');
