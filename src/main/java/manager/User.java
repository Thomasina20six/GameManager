package manager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import shared.utils.EncryptionUtil;

public class User {
    private String username;
    private String password;

    private static final String ACCOUNTS_FILE = "data/user_accounts.txt";

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Load all users from file
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(ACCOUNTS_FILE);

        try {
            if (!file.exists()) {
                file.createNewFile();
                return users;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    users.add(new User(username, password));
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }

    public static boolean login(String username, String password) {
        List<User> users = loadUsers();
        String encryptedUsername = EncryptionUtil.encrypt(username);
        String encryptedPassword = EncryptionUtil.encrypt(password);

        for (User user : users) {
            if (user.getUsername().equals(encryptedUsername) && user.getPassword().equals(encryptedPassword)) {
                return true;
            }
        }

        return false;
    }

    // Check whether a username already exists
    public static boolean usernameExists(String username) {
        List<User> users = loadUsers();

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    // Save a new account
    public static boolean createAccount(String username, String password) {
        String encryptedUsername = EncryptionUtil.encrypt(username);
        String encryptedPassword = EncryptionUtil.encrypt(password);

        if (usernameExists(encryptedUsername)) {
            return false;
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE, true));
            writer.write(encryptedUsername + "," + encryptedPassword);
            writer.newLine();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}