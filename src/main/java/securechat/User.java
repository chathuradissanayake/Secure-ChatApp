package securechat;

import org.mindrot.jbcrypt.BCrypt;

public class User {
    private String username;
    private String hashedPassword;

    public User(String username, String password) {
        this.username = username.toLowerCase(); // Normalize username to lowercase
        // Hash the password using BCrypt
        this.hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Authenticate by comparing hashed password
    public boolean authenticate(String password) {
        return BCrypt.checkpw(password, this.hashedPassword);
    }

    public String getUsername() {
        return username;
    }
}
