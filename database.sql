DROP TABLE IF EXISTS friend_requests;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS activities;
DROP TABLE IF EXISTS trips;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE trips (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE TABLE activities (
    id SERIAL PRIMARY KEY,
    trip_id INT NOT NULL,
    activity_date DATE NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    price DOUBLE PRECISION DEFAULT 0,
    FOREIGN KEY (trip_id) REFERENCES trips(id)
);

CREATE TABLE friend_requests (
    id SERIAL PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id)
);

CREATE TABLE friends (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    friend_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (friend_id) REFERENCES users(id)
);

INSERT INTO users (username, password)
VALUES ('admin', 'admin123'),
        ('fred', 'fred'),
       ('alex', 'alex'),
        ('miu', 'miu');

INSERT INTO trips (title, destination, start_date, end_date, user_id)
VALUES ('Spring Seoul Trip', 'Seoul', '2026-04-10', '2026-04-20', 1),
        ('Winter Salou Trip', 'Salou', '2026-01-05', '2026-01-10', 1);

INSERT INTO activities (trip_id, activity_date, type, title, description, price)
VALUES (1, '2026-04-10', 'Hotel', 'Hotel Check-in', 'Check-in at Myeongdong Hotel', 320),
    (1, '2026-04-11', 'Sightseeing', 'Gyeongbokgung Palace', 'Visit royal palace', 15),
    (1, '2026-04-11', 'Food', 'Korean BBQ Dinner', 'Dinner in Hongdae', 45),
    (1, '2026-04-12', 'Transport', 'AREX Train', 'Airport railroad ticket', 12);

INSERT INTO friends (user_id, friend_id)
VALUES (1, 2), (2, 1);