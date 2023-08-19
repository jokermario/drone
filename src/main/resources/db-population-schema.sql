DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS medication;
DROP TABLE IF EXISTS drone;
SET TIME ZONE 'Africa/Lagos';

CREATE TABLE users (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL,
    roles VARCHAR(100) NOT NULL
);

CREATE TABLE drones (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    serial_no VARCHAR(100) NOT NULL,
    model VARCHAR(13) NOT NULL,
    weight_limit INT NOT NULL,
    battery_capacity INTEGER NOT NULL,
    state VARCHAR(10) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT serial_no_unique UNIQUE (serial_no)
);

CREATE TABLE medications (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    weight INT NOT NULL,
    code VARCHAR(100) NOT NULL,
    image VARCHAR(500) NOT NULL,
    drone_serial_no VARCHAR(100),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_drone_serial_no
        FOREIGN KEY (drone_serial_no)
            REFERENCES drones(serial_no),
    CONSTRAINT code_unique UNIQUE (code)
);