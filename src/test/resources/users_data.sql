-- User ivan@email.com/pass
INSERT INTO users (username, password, enabled)
VALUES ('IvanDrago-temp', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', true);

INSERT INTO user_profiles (username, first_name, last_name, email)
VALUES ('IvanDrago-temp', 'Ivan', 'Drago', 'ivan@email.com');

INSERT INTO authorities (username, authority)
VALUES ('IvanDrago-temp', 'ROLE_USER');

INSERT INTO group_members (group_id, username)
VALUES (1, 'IvanDrago-temp');
