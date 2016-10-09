package se.olz.myfootprints;

public class User {
    private static String email;
    private static String token;

    public User(String email, String token) {
        User.email = email;
        User.token = token;
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
}
