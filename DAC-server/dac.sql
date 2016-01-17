DROP DATABASE dac_ksaurav;
CREATE DATABASE dac_ksaurav;

USE dac_ksaurav;

CREATE TABLE dac (
    expression text,
    result varchar(40),
    last_used int NOT NULL
);
