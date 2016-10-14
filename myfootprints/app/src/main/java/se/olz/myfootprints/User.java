package se.olz.myfootprints;

public class User {
    private static String email;
    private static String token;
    private static int serverLastId;

    public User(String email, String token, int lastId) {
        User.email = email;
        User.token = token;
        User.serverLastId = lastId;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        User.email = email;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        User.token = token;
    }

    public static int getServerLastId() {
        return serverLastId;
    }

    public static void setServerLastId(int serverLastId) {
        User.serverLastId = serverLastId;
    }
}
