# Java Socket-Based Messaging Application

## Authors

* Student 1: DARPAN
* Student 2: BHAWESH 

## Project Overview

This project is a real-time messaging application developed using Java Socket Programming and a Client-Server Architecture. The application allows multiple users to communicate through chat rooms in real time.

The system demonstrates key networking concepts, including:

* TCP socket communication
* Multi-client handling using threads
* Client-Server Architecture
* Chat room management
* User invitations
* Real-time messaging
* Configuration management
* Logging and error handling

---

## Features

### User Management

* Unique usernames
* Multiple simultaneous users
* View online users

### Chat Rooms

* Default chat rooms:

  * general
  * study
* Create new chat rooms
* Join existing chat rooms
* Switch between chat rooms
* Leave chat rooms
* View users in a room

### Messaging

* Real-time group messaging
* Multiple users communicating simultaneously
* Room-based message broadcasting

### Invitations

* Invite users to specific chat rooms

### System Features

* Configuration file support
* Server logging
* Error handling
* Modular code structure

---

## System Requirements

### Software Requirements

* Java JDK 17 or later
* Visual Studio Code
* Windows 10/11, Linux, or macOS

---

## Project Structure

```text
MessagingApp/
│
├── config/
│   └── server.properties
│
├── logs/
│   └── chat-server.log
│
├── src/
│   └── chatapp/
│       ├── common/
│       │   └── Protocol.java
│       │
│       ├── server/
│       │   ├── ServerConfig.java
│       │   ├── ChatServer.java
│       │   └── ClientHandler.java
│       │
│       └── client/
│           └── ChatClient.java
│
└── out/
```

---

## Installation Guide

### Step 1: Open the Project

Open Visual Studio Code and load the project folder:

```text
MessagingApp
```

### Step 2: Open a Terminal

Open a terminal in VS Code:

```text
Terminal → New Terminal
```

### Step 3: Compile the Application

Run the following command:

```bash
mkdir out

javac -d out src\chatapp\common\Protocol.java src\chatapp\server\ServerConfig.java src\chatapp\server\ChatServer.java src\chatapp\server\ClientHandler.java src\chatapp\client\ChatClient.java
```

If the `out` folder already exists, the warning can be ignored.

---

## Running the Application

### Start the Server

Open a terminal and run:

```bash
java -cp out chatapp.server.ChatServer
```

Expected output:

```text
Chat server started on port 5000
```

---

### Start Client 1

Open a new terminal and run:

```bash
java -cp out chatapp.client.ChatClient
```

Example username:

```text
john
```

---

### Start Client 2

```bash
java -cp out chatapp.client.ChatClient
```

Example username:

```text
jack
```

---

### Start Client 3

```bash
java -cp out chatapp.client.ChatClient
```

Example username:

```text
jonson
```

---

## Available Commands

| Command                     | Description                    |
| --------------------------- | ------------------------------ |
| `/rooms`                    | Show available chat rooms      |
| `/users`                    | Show online users              |
| `/who`                      | Show users in the current room |
| `/create roomName`          | Create a new room              |
| `/join roomName`            | Join an existing room          |
| `/switch roomName`          | Switch to another room         |
| `/invite username roomName` | Invite a user to a room        |
| `/leave`                    | Leave the current room         |
| `/help`                     | Show available commands        |
| `/quit`                     | Exit the application           |

---

## Example Demonstration

Create a room:

```text
/create project
```

Invite a user:

```text
/invite john project
```

Join the room:

```text
/join project
```

Send a message:

```text
Hello everyone
```

Display users in the room:

```text
/who
```

Display all online users:

```text
/users
```

---

## Configuration

The server configuration is stored in:

```text
config/server.properties
```

Example configuration:

```properties
server.port=5000
default.rooms=general,study
max.clients=50
log.file=logs/chat-server.log
```

---

## Build Information

* Programming Language: Java
* Architecture: Client-Server
* Communication Protocol: TCP
* Interface: Console-Based
* Operating System Support: Windows, Linux, macOS

---

## License

This project was developed for academic purposes as part of a Network Programming module.
