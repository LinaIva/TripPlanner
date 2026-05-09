# TripPlanner

TripPlanner is a Java web application for collaborative trip planning.  
Users can register, log in, create trips, add activities, manage friends, invite friends into trips, write trip notes, and chat in real time inside a trip.

This project was created as an academic project for an Advanced Java Web Application course.

## Main Features

- user registration and login
- session-based authentication
- cookie support for storing the last logged-in username
- trip creation and trip overview
- trip details page with activities and total cost
- trip notes stored via JPA
- friends list and friend requests
- adding friends into a trip
- real-time trip chat via WebSocket
- background logging thread for active users

## Technologies Used

- Java Servlets
- JDBC
- JPA (EclipseLink)
- WebSocket
- PostgreSQL
- Apache Tomcat 10

## Project Structure

- `servlets/` - web layer and request handling
- `dao/` - JDBC data access classes
- `database/` - database connection
- `jpa/` - JPA entity and JPA service
- `util/` - helper classes, active user tracking, background thread
- `websocket/` - WebSocket endpoint
- `database.sql` - database schema and sample data

## Prerequisites

Before running the project, make sure you have:

- Java installed
- PostgreSQL installed and running
- Apache Tomcat 10 installed

## Database Setup

The application reads database credentials from environment variables or a local `.env` file.

Create a local `.env` file in the project root:

```text
DB_URL=jdbc:postgresql://localhost:5432/university
DB_USER=your_db_user
DB_PASSWORD=your_password_here
```

You can also use `.env.example` as a template.

Default local configuration used in development:

- database: `university`
- username: your local PostgreSQL username
- password: your local PostgreSQL password

Create the database and run the SQL script:

```sql
CREATE DATABASE university;
```

Then execute:

```bash
psql -U your_db_user -d university -f database.sql
```

The script creates all required tables and inserts sample data.

## Sample Accounts

These users are inserted by `database.sql`:

- `admin / admin123`
- `fred / fred`
- `alex / alex`
- `lisa / lisa`

## JPA Configuration

This project uses JPA for trip notes.  
Make sure your `persistence.xml` is available in the deployed application, usually here:

```text
WEB-INF/classes/META-INF/persistence.xml
```

The persistence unit name used by the project is:

```text
tripplannerPU
```

## Deployment Structure

The application is intended to be deployed as an exploded web application in Tomcat, for example:

```text
/path/to/apache-tomcat/webapps/tripplanner
```

## Compilation

If you compile manually into Tomcat, use a command that includes all source folders used by the application:

```bash
javac -cp "/path/to/apache-tomcat/lib/*" -d "/path/to/apache-tomcat/webapps/tripplanner/WEB-INF/classes" servlets/*.java dao/*.java database/*.java jpa/*.java util/*.java websocket/*.java
```

If you only compile `servlets`, `dao`, and `database`, then JPA, utility classes, and WebSocket classes must already be present in `WEB-INF/classes`.

## Running the Application

1. Start PostgreSQL.
2. Make sure the `university` database exists.
3. Run `database.sql`.
4. Place the project into Tomcat as `tripplanner`.
5. Make sure `.env` exists in the project root. The application also supports reading it from the deployed Tomcat application folder if needed.
6. Make sure JPA configuration and required libraries are available in the deployment.
7. Compile the source files into:

```text
/path/to/apache-tomcat/webapps/tripplanner/WEB-INF/classes
```

8. Start Tomcat.
9. Open the application in your browser:

```text
http://localhost:8080/tripplanner/
```

## Application Flow

Typical usage flow:

1. Open the login page.
2. Log in with an existing account or register a new one.
3. Create a new trip or open an existing one.
4. Add activities to the trip.
5. View total trip cost.
6. Add notes to the trip.
7. Manage friends and send friend requests.
8. Add friends into the trip.
9. Use the trip chat for real-time communication.

## Implemented Course Requirements

This project includes:

- multiple servlets with separate responsibilities
- authentication and session management
- cookie usage
- JDBC database access
- JPA entity and persistence logic
- multithreading through a background logger thread
- WebSocket communication
- servlet interaction through shared session data
- dynamically generated HTML responses

## Notes

- The project is intentionally implemented in a lecture-aligned servlet style.
- It is not based on Spring or Spring Boot.
- The current UI is simple because the main goal of the project is demonstrating Java web technologies covered in the course.
- `.env` is ignored by git so local database passwords are not committed.
