package securechat;

import java.net.*;
import java.io.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error initializing ClientHandler: " + e.getMessage());
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendRawMessage(String msg) {
        try {
            dos.writeUTF(msg);
        } catch (IOException e) {
            System.out.println("Error sending message to " + username);
        }
    }

    @Override
    public void run() {
        try {
            dos.writeUTF("Enter username: ");
            String user = dis.readUTF().toLowerCase();
            dos.writeUTF("Enter password: ");
            String pass = dis.readUTF();

            User u = Server.getUser(user);
            if (u != null && u.authenticate(pass)) {
                this.username = u.getUsername();
                dos.writeUTF("AUTH_SUCCESS");
                System.out.println(username + " connected.");

                while (true) {
                    String input = dis.readUTF();
                    if ("exit".equalsIgnoreCase(input)) {
                        break;
                    }

                    if (input.contains("::")) {
                        String[] parts = input.split("::", 2);
                        if (parts.length == 2) {
                            String recipient = parts[0].toLowerCase();
                            String encryptedMsg = parts[1];
                            Server.forwardEncryptedMessage(encryptedMsg, this, recipient);
                        } else {
                            dos.writeUTF("Invalid message format.");
                        }
                    } else {
                        dos.writeUTF("Invalid message format.");
                    }
                }
            } else {
                dos.writeUTF("AUTH_FAIL");
            }
        } catch (IOException e) {
            System.out.println("Connection error with " + username);
        } finally {
            Server.removeClient(this);
            try {
                if (socket != null) socket.close();
            } catch (IOException ignored) {}
        }
    }
}
