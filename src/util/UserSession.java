package util;

import model.User;

/**
 * Utility class untuk menyimpan data user yang sedang login.
 */
public class UserSession {
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void clearSession() {
        currentUser = null;
    }
}
