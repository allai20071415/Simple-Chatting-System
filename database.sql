-- Run this inside the Nimbus database: db_454cnbsqh

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    sender VARCHAR(50) NOT NULL,
    receiver VARCHAR(50) NOT NULL,
    message VARCHAR(500) NOT NULL,
    message_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (username) VALUES
('dharshini'),
('pooja'),
('jessi'),
('pavi'),
('ragavi')
ON DUPLICATE KEY UPDATE username = VALUES(username);
