CREATE SCHEMA IF NOT EXISTS blablacar;

CREATE TABLE blablacar.users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE blablacar.ads (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    departure_city VARCHAR(255),
    departure_time TIME,
    arrival_city VARCHAR(255),
    arrival_time TIME,
    date DATE,
    car VARCHAR(255),
    max_seats INT,
    booked_seats INT,
    two_back_seats BOOLEAN,
    publisher_id BIGINT,
    CONSTRAINT fk_publisher FOREIGN KEY (publisher_id) REFERENCES blablacar.users(id)
);

CREATE TABLE blablacar.bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    ad_id BIGINT,
    CONSTRAINT fk_booker FOREIGN KEY (user_id) REFERENCES blablacar.users(id),
    CONSTRAINT fk_ad FOREIGN KEY (ad_id) REFERENCES blablacar.ads(id)
);

INSERT INTO blablacar.users (username, first_name, last_name, email) VALUES
    ('jdoe', 'John', 'Doe', 'jdoe@example.com'),
    ('asmith', 'Alice', 'Smith', 'asmith@example.com');

INSERT INTO blablacar.ads (departure_city, departure_time, arrival_city, arrival_time, date, car, max_seats, booked_seats, two_back_seats, publisher_id) VALUES
    ('New York', '08:00:00', 'Boston', '12:00:00', '2023-07-20', 'Toyota Camry', 4, 2, false, 1),
    ('Los Angeles', '09:00:00', 'San Francisco', '13:00:00', '2023-07-21', 'Honda Civic', 4, 3, true, 2);

INSERT INTO blablacar.bookings (user_id, ad_id) VALUES
    (1, 1),
    (2, 2);
