CREATE TABLE IF NOT EXISTS schools
(
    id            BIGSERIAL PRIMARY KEY,
    school_name   VARCHAR(255) NOT NULL,
    logo_filename VARCHAR(500),
    created_at    TIMESTAMP DEFAULT now(),
    owner_id      VARCHAR(50) UNIQUE, -- 1:1 link
    CONSTRAINT fk_school_user FOREIGN KEY (owner_id) REFERENCES users (username) ON DELETE CASCADE
);
