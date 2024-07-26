CREATE SCHEMA IF NOT EXISTS blablacar;

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
    publisher_id VARCHAR(255)
);

CREATE TABLE blablacar.bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255),
    ad_id BIGINT,
    CONSTRAINT fk_ad FOREIGN KEY (ad_id) REFERENCES blablacar.ads(id) ON DELETE CASCADE
);


