package config;

/**
 * Session management untuk menyimpan data user yang sedang login.
 * Menyimpan role (Admin/Dosen/Mahasiswa) dan ID user aktif.
 */
public class Session {

    private static String currentUserId;
    private static String currentUserName;
    private static String currentRole; // "Admin", "Dosen", "Mahasiswa"

    public static void setSession(String userId, String userName, String role) {
        currentUserId = userId;
        currentUserName = userName;
        currentRole = role;
    }

    public static String getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentUserName() {
        return currentUserName;
    }

    public static String getCurrentRole() {
        return currentRole;
    }

    public static void clearSession() {
        currentUserId = null;
        currentUserName = null;
        currentRole = null;
    }

    public static boolean isLoggedIn() {
        return currentUserId != null;
    }
}
