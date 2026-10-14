# Social Media API

Java 11 social media backend using Javalin, JDBC, and H2.

## Run locally

Install JDK 11 and Maven, then run commands from the repository root:

```sh
mvn test
mvn compile dependency:build-classpath -Dmdep.outputFile=target/classpath.txt
java -cp "target/classes:$(cat target/classpath.txt)" Main
```

The server listens on `http://localhost:8080`. The H2 database is stored in
`h2/`, which is excluded from Git. The provided connection utility resets the
schema and inserts sample data when the application first connects; this is a
learning project, so do not use its database for durable production data.

## API behavior

| Method | Endpoint | Behavior |
| --- | --- | --- |
| POST | `/register` | Register a unique username with a password of at least 4 characters |
| POST | `/login` | Return the matching account or HTTP 401 |
| POST | `/messages` | Create a message for an existing account with 1–254 characters |
| GET | `/messages` | List messages in message ID order |
| GET | `/messages/{message_id}` | Return a message or an empty HTTP 200 response |
| DELETE | `/messages/{message_id}` | Remove and return a message; repeated deletion returns an empty HTTP 200 |
| PATCH | `/messages/{message_id}` | Update message text, preserving author and timestamp |
| GET | `/accounts/{account_id}/messages` | List an account's messages in message ID order |

Malformed JSON and nonnumeric path IDs return HTTP 400. Invalid registration,
message creation, and updates also return HTTP 400. Collection endpoints return
an empty JSON array when no records match.

```sh
curl -X POST http://localhost:8080/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"demo","password":"demo1234"}'
curl http://localhost:8080/messages
```

## Development

Controllers handle HTTP, services validate input, and DAOs execute parameterized
SQL. `ServiceValidationTest` exercises missing fields and length boundaries with
a mocked DAO. HTTP integration tests reset the database between cases and use
port 8080; stop any running application before executing `mvn test`.

The educational API returns account passwords as required by its original tests
and does not implement sessions or message ownership checks. Production use
requires a redesigned authentication and password-storage contract.
