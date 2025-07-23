# SecureChatApp

SecureChatApp is a Java-based secure chat application using AES encryption for end-to-end encrypted messaging between clients. It supports multiple users with authentication and encrypted communication over a TCP connection.

## Features

- User authentication with BCrypt hashed passwords
- AES encrypted messaging between clients
- Multiple clients supported (Alice, Bob, Charlie, etc.)
- Server forwards encrypted messages without decrypting
- Runs over TCP sockets on configurable port
- Works on local network (LAN)

## Setup and Usage

### Prerequisites

- Java JDK 8 or above
- Gradle (optional, if using build scripts)
- Devices connected on the same local network (LAN)

### 1. Build and Run Server

- Compile and run `Server.java` on the host machine.
- This machine acts as the chat server.

### 2. Obtain Server IP Address

- Find the local IP address of the server machine.
- On Windows: run `ipconfig` and find IPv4 address.
- On Linux/macOS: run `ifconfig` or `ip a`.

### 3. Configure Clients

- In client files (`Client1.java`, `Client2.java`, `Client3.java`), update the server IP in the socket connection line:
  ```java
  socket = new Socket("SERVER_IP_ADDRESS", 5000);
  ```
- Replace `SERVER_IP_ADDRESS` with the actual IP found above.

### 4. Run Clients

- Compile and run client programs on different devices on the same LAN.
- Supported usernames: `alice`, `bob`, `charlie` (passwords hardcoded in server).
- Authenticate and chat securely with AES encryption.

### 5. Sending Messages

- After login, enter the recipient's username to send encrypted messages.
- Messages are encrypted on the sender side and decrypted on the recipient side.
- The server only forwards encrypted messages and never decrypts them.

## User Credentials (hardcoded)

| Username | Password     |
|----------|--------------|
| alice    | password123  |
| bob      | password456  |
| charlie  | password789  |

## AES Encryption Keys

Each sender-recipient pair uses a shared AES key for encryption and decryption.

Example keys (16-byte strings):

- alice -> bob : `alicebobkey12345`
- bob -> alice : `bobalicekey12345`
- charlie -> bob : `charbobkey123456`
- bob -> charlie : `bobcharkey123456`
- charlie -> alice : `charalicekey123456`
- alice -> charlie : `alicecharkey123456`

## Notes

- Ensure port 5000 is open on the server machine's firewall.
- Run server before starting clients.
- Clients must be on the same LAN as the server.
- To add new users, modify the server's user database and update encryption keys in clients accordingly.

## License

MIT License

---

Feel free to customize the keys and users for your needs.
