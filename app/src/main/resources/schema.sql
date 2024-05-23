DROP TABLE IF EXISTS url_checks;
DROP TABLE IF EXISTS urls;

CREATE TABLE urls (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP
);

CREATE TABLE url_checks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    url_id BIGINT REFERENCES urls(id) NOT NULL,
    status_code INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    h1 VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP
);