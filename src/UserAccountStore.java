import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class UserAccountStore {
    private static final String STORAGE_FILE_NAME = "numberbomb_users.properties";
    private static final UserAccountStore INSTANCE = new UserAccountStore();

    private final File storageFile;

    private UserAccountStore() {
        this.storageFile = new File(STORAGE_FILE_NAME);
        ensureStorageFile();
    }

    public static UserAccountStore getInstance() {
        return INSTANCE;
    }

    public synchronized boolean registerUser(String username, String password) {
        String normalizedUsername = UserAccount.normalizeUsername(username);
        if (!UserAccount.isValidUsername(normalizedUsername)) {
            return false;
        }
        if (!UserAccount.isValidPassword(password)) {
            return false;
        }
        String loginName = UserAccount.normalizeLoginName(normalizedUsername);
        Properties properties = loadProperties();
        if (properties.containsKey(loginName)) {
            return false;
        }

        String salt = UserAccount.generateSalt();
        String hash = UserAccount.hashPassword(password, salt);
        properties.setProperty(loginName, salt + ":" + hash);
        saveProperties(properties);
        return true;
    }

    public synchronized boolean validateLogin(String username, String password) {
        String normalizedUsername = UserAccount.normalizeUsername(username);
        if (!UserAccount.isValidUsername(normalizedUsername)) {
            return false;
        }
        if (!UserAccount.isValidPassword(password)) {
            return false;
        }

        String loginName = UserAccount.normalizeLoginName(normalizedUsername);
        Properties properties = loadProperties();
        String storedValue = properties.getProperty(loginName);
        if (storedValue == null || storedValue.isEmpty()) {
            return false;
        }

        String[] parts = storedValue.split(":", 2);
        if (parts.length != 2) {
            return false;
        }

        String salt = parts[0];
        String storedHash = parts[1];
        String computedHash = UserAccount.hashPassword(password, salt);
        return storedHash.equals(computedHash);
    }

    public synchronized boolean usernameExists(String username) {
        String normalizedUsername = UserAccount.normalizeUsername(username);
        if (!UserAccount.isValidUsername(normalizedUsername)) {
            return false;
        }
        return loadProperties().containsKey(UserAccount.normalizeLoginName(normalizedUsername));
    }

    public synchronized List<String> getUsernames() {
        Properties properties = loadProperties();
        List<String> usernames = new ArrayList<>();
        for (String key : properties.stringPropertyNames()) {
            usernames.add(key);
        }
        Collections.sort(usernames);
        return usernames;
    }

    public File getStorageFile() {
        return storageFile;
    }

    private void ensureStorageFile() {
        if (!storageFile.exists()) {
            try {
                storageFile.createNewFile();
            } catch (IOException ignored) {
                // No-op: if the file cannot be created, the app will still function in-memory on this run.
            }
        }
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        if (!storageFile.exists()) {
            return properties;
        }
        try (FileInputStream input = new FileInputStream(storageFile)) {
            properties.load(input);
        } catch (IOException ignored) {
            return new Properties();
        }
        return properties;
    }

    private void saveProperties(Properties properties) {
        try (FileOutputStream output = new FileOutputStream(storageFile)) {
            properties.store(output, "NumberBomb users");
        } catch (IOException ignored) {
            // No-op: verification and login can still work in current JVM, but persistence is best effort.
        }
    }
}
