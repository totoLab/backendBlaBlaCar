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
    CONSTRAINT fk_ad FOREIGN KEY (ad_id) REFERENCES blablacar.ads(id) ON DELETE CASCADE
);

INSERT INTO blablacar.users (username, first_name, last_name, email) VALUES
    ('jdoe', 'John', 'Doe', 'jdoe@example.com'),
    ('asmith', 'Alice', 'Smith', 'asmith@example.com');

INSERT INTO blablacar.ads (departure_city, departure_time, arrival_city, arrival_time, date, car, max_seats, booked_seats, two_back_seats, publisher_id) VALUES
    ('New York', '08:00:00', 'Boston', '12:00:00', '2024-08-20', 'Toyota Camry', 4, 2, false, 1),
    ('Los Angeles', '09:00:00', 'San Francisco', '13:00:00', '2024-08-21', 'Honda Civic', 4, 3, true, 2);

INSERT INTO blablacar.bookings (user_id, ad_id) VALUES
    (1, 1),
    (2, 2);

INSERT INTO blablacar.users (username, first_name, last_name, email) VALUES
    ('bwayne', 'Bruce', 'Wayne', 'bwayne@example.com'),
    ('ckent', 'Clark', 'Kent', 'ckent@example.com'),
    ('kdanvers', 'Kara', 'Danvers', 'kdanvers@example.com'),
    ('pparker', 'Peter', 'Parker', 'pparker@example.com');

INSERT INTO blablacar.ads (departure_city, departure_time, arrival_city, arrival_time, date, car, max_seats, booked_seats, two_back_seats, publisher_id) VALUES
    ('Gotham', '07:00:00', 'Metropolis', '11:00:00', '2024-08-22', 'Batmobile', 2, 1, false, 3),
    ('Smallville', '10:00:00', 'Metropolis', '12:00:00', '2024-08-23', 'Farm Truck', 3, 2, false, 4),
    ('National City', '09:30:00', 'Central City', '11:30:00', '2024-08-24', 'Jet', 4, 4, true, 5),
    ('Queens', '06:00:00', 'Manhattan', '07:00:00', '2024-08-25', 'Spider-Mobile', 2, 1, false, 6),
    ('Gotham', '15:00:00', 'Central City', '18:00:00', '2024-08-26', 'SUV', 5, 3, true, 3);

-- Insert additional bookings
INSERT INTO blablacar.bookings (user_id, ad_id) VALUES
    (1, 3),
    (2, 4),
    (5, 5),
    (6, 6),
    (3, 7),
    (4, 1),
    (1, 4),
    (2, 5);
