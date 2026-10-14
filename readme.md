# Social Media Blog API

## Background

I developed this project as a backend for a social media application. It manages user accounts and messages, allowing users to register, log in, create posts, and retrieve or delete messages.
My focus is on processing requests, validating input, and persisting data in a database. This project does not include a frontend or session-token authentication.
Database Tables
Account
account_id integer primary key auto_increment,
username varchar(255) unique,
password varchar(255)

Message
message_id integer primary key auto_increment,
posted_by integer,
message_text varchar(255),
time_posted_epoch long,
foreign key (posted_by) references Account(account_id)

API Requirements
I use http://localhost:8080 as the base URL.
1. User Registration
POST /register
I validate that the username is not blank, the password contains at least four characters, and the username is not already registered.
Successful registration saves the account and returns its JSON representation, including the generated account_id, with 200 OK. Invalid registration returns 400 Bad Request.
2. User Login
POST /login
I check whether the submitted username and password match an existing account.
A successful login returns the account as JSON, including its account_id, with 200 OK. Incorrect credentials return 401 Unauthorized.
3. Create a Message
POST /messages
I validate that message_text is not blank, contains no more than 255 characters, and that posted_by identifies an existing account.
A valid message is saved and returned as JSON with its generated message_id and 200 OK. Invalid requests return 400 Bad Request.
4. Retrieve All Messages
GET /messages
I return all stored messages as a JSON array with 200 OK. When no messages exist, the response contains an empty array.
5. Retrieve a Message by ID
GET /messages/{message_id}
I return the matching message as JSON with 200 OK. If the message does not exist, I return an empty response body with the same status.
6. Delete a Message
DELETE /messages/{message_id}
I remove the matching message from the database and return the deleted message as JSON with 200 OK.
If the message does not exist, I return 200 OK with an empty response body. Repeating the request leaves the message deleted.
