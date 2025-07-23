package securechat;

import java.net.*;
import java.io.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Client3 {
    private static final Map<String, String> encryptionKeys = Map.of(
            "alice->bob", "alicebobkey12345",   // 16 chars
            "bob->alice", "bobalicekey12345",  // 16 chars
            "charlie->bob", "charbobkey123456",  // 16 characters
            "bob->charlie", "bobcharkey123456" ,  // 16 characters
            "charlie->alice", "char2alicekey789",    //  16 chars
            "alice->charlie", "alicetocharlie99"   // 16 chars

    );

    private static String username;

    private static String encrypt(String msg, String recipient) throws Exception {
        String keyStr = encryptionKeys.get(username.toLowerCase() + "->" + recipient.toLowerCase());
        if (keyStr == null) throw new IllegalArgumentException("No encryption key for this recipient");
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey key = new SecretKeySpec(keyStr.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(msg.getBytes()));
    }

    private static String decrypt(String msg, String sender) throws Exception {
        String keyStr = encryptionKeys.get(sender.toLowerCase() + "->" + username.toLowerCase());
        if (keyStr == null) throw new IllegalArgumentException("No decryption key for this sender");
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey key = new SecretKeySpec(keyStr.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(msg)));
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Socket socket = null;

        try {
            socket = new Socket("localhost", 5000);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            System.out.print(dis.readUTF());
            username = sc.nextLine();
            dos.writeUTF(username);

            System.out.print(dis.readUTF());
            dos.writeUTF(sc.nextLine());

            String response = dis.readUTF();
            if ("AUTH_SUCCESS".equals(response)) {
                System.out.println("Logged in successfully!");

                Thread readThread = new Thread(() -> {
                    try {
                        while (true) {
                            String msg = dis.readUTF();

                            if (msg.contains("::")) {
                                String[] parts = msg.split("::", 2);
                                if (parts.length == 2) {
                                    String sender = parts[0];
                                    String encryptedMsg = parts[1];
                                    try {
                                        String decrypted = decrypt(encryptedMsg, sender);
                                        System.out.println("\n" + sender + ": " + decrypted);
                                    } catch (Exception e) {
                                        System.out.println("\n" + sender + " (Encrypted): " + encryptedMsg);
                                    }
                                } else {
                                    System.out.println("\nMalformed message: " + msg);
                                }
                            } else {
                                System.out.println("\n" + msg);
                            }
                            System.out.print("Send to (username) ");
                        }
                    } catch (IOException e) {
                        System.out.println("Disconnected from server.");
                    }
                });
                readThread.start();

                while (true) {
                    System.out.print("Send to (username): ");
                    String recipient = sc.nextLine();
                    if ("exit".equalsIgnoreCase(recipient)) {
                        dos.writeUTF("exit");
                        break;
                    }

                    System.out.print("Message: ");
                    String msg = sc.nextLine();
                    if ("exit".equalsIgnoreCase(msg)) {
                        dos.writeUTF("exit");
                        break;
                    }

                    try {
                        String encryptedMsg = encrypt(msg, recipient);
                        dos.writeUTF(recipient + "::" + encryptedMsg);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Encryption error: " + e.getMessage());
                    }
                }
            } else {
                System.out.println("Login failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException ignored) {}
            sc.close();
        }
    }
}