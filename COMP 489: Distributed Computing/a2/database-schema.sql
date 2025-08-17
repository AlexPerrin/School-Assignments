CREATE DATABASE fileshare;
\c fileshare;

CREATE TABLE files (
    file_name VARCHAR(255) PRIMARY KEY,
    client_address VARCHAR(255) NOT NULL,
    client_port INT NOT NULL
);