package securechat;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final Map<String, User> userDB = new HashMap<>();
    private static final Set<ClientHandler> clientHandlers = ConcurrentHashMap.newKeySet();
    private static final int PORT = 5000;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started...");

        // Hardcoded users
        userDB.put("alice", new User("alice", "password123"));
        userDB.put("bob", new User("bob", "password456"));

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket);
            clientHandlers.add(clientHandler);
            new Thread(clientHandler).start();
        }
    }

    public static User getUser(String username) {
        return userDB.get(username);
    }

    public static void forwardEncryptedMessage(String encryptedMsg, ClientHandler sender, String recipient) {
        System.out.println("Encrypted message from " + sender.getUsername() + " to " + recipient + ": " + encryptedMsg);
        for (ClientHandler client : clientHandlers) {
            if (client.getUsername() != null && client.getUsername().equalsIgnoreCase(recipient)) {
                client.sendRawMessage(sender.getUsername() + "::" + encryptedMsg);
                break;
            }
        }
    }

    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }
}
