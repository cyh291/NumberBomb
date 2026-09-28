public class AuthSession {
    private static String currentUser = null;

    public static synchronized void setCurrentUser(String username) {
        currentUser = username == null ? null : UserAccount.normalizeUsername(username);
    }

    public static synchronized String getCurrentUser() {
        return currentUser;
    }

    public static synchronized boolean isLoggedIn() {
        return currentUser != null && !currentUser.trim().isEmpty();
    }

    public static synchronized void logout() {
        currentUser = null;
    }
}
