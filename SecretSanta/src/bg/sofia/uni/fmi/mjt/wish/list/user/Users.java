package bg.sofia.uni.fmi.mjt.wish.list.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Users implements UserCollection {
    private static final Map<String, String> usersWishes = new HashMap<>();
    private static final Set<String> loggedUsers = new HashSet<>();

    private static final String NL = System.lineSeparator();

    public void registerUser(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Arguments must not be null!" + NL);
        }
        usersWishes.put(username, password);
        loggedUsers.add(username);
    }

    public void loginUser(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null!" + NL);
        }
        loggedUsers.add(username);
    }

    public void logoutUser(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null!" + NL);
        }
        loggedUsers.remove(username);
    }

    public boolean isRegistered(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null!" + NL);
        }
        return usersWishes.containsKey(username);
    }

    public boolean isCorrectUserPassCombination(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Arguments must not be null!" + NL);
        }
        return usersWishes.containsKey(username)
                && usersWishes.get(username).equals(password);
    }

    public static boolean isValidUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null!" + NL);
        }
        return username.matches("[a-zA-Z0-9-_.]+");
    }

    public boolean isUsernameTaken(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null!" + NL);
        }
        return usersWishes.containsKey(username);
    }

    public boolean isLogged(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null!" + NL);
        }
        return loggedUsers.contains(username);
    }

    public int getNumberOfRegisteredUsers() {
        return usersWishes.size();
    }

    public int getNumberOfLoggedUsers() {
        return loggedUsers.size();
    }
}
