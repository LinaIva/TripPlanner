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

INSERT INTO users (username, password)
VALUES ('admin', 'admin123');

INSERT INTO trips (title, destination, start_date, end_date, user_id)
VALUES ('Spring Seoul Trip', 'Seoul', '2026-04-10', '2026-04-20', 1);

INSERT INTO activities (trip_id, activity_date, type, title, description, price)
VALUES
    (1, '2026-04-10', 'Hotel', 'Hotel Check-in', 'Check-in at Myeongdong Hotel', 320),
    (1, '2026-04-11', 'Sightseeing', 'Gyeongbokgung Palace', 'Visit royal palace', 15),
    (1, '2026-04-11', 'Food', 'Korean BBQ Dinner', 'Dinner in Hongdae', 45),
    (1, '2026-04-12', 'Transport', 'AREX Train', 'Airport railroad ticket', 12);