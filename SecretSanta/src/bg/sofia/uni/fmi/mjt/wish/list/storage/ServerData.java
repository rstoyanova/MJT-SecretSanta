package bg.sofia.uni.fmi.mjt.wish.list.storage;

import bg.sofia.uni.fmi.mjt.wish.list.user.Users;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerData implements DataStorage {
    private static final Map<String, List<String>> wishList = new HashMap<>();
    private static final Map<SocketChannel, String> userSockets = new HashMap<>();
    private static final Users users = new Users();

    public List<String> removeWishesFor(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null!");
        }
        return wishList.remove(username);
    }

    public void addWish(String username, String wish) {
        if (username == null || wish == null) {
            throw new IllegalArgumentException("Arguments must not be null!");
        }
        if (wishList.get(username) == null) {
            List<String> list = new ArrayList<>();
            list.add(wish);
            wishList.put(username, list);
        } else {
            if (!wishIsAlreadySubmittedForUser(username, wish)) {
                wishList.get(username).add(wish);
            }
        }
    }

    public void addWishes(String username, List<String> wishes) {
        if (username == null || wishes == null) {
            throw new IllegalArgumentException("Arguments must not be null!");
        }
        if (wishList.containsKey(username)) {
            wishList.get(username).addAll(wishes);
        } else {
            wishList.put(username, wishes);
        }
    }

    public void registerUser(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Arguments must not be null!");
        }
        users.registerUser(username, password);
    }

    public void loginUser(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null!");
        }
        users.loginUser(username);
    }

    public void logoutUser(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null!");
        }
        users.logoutUser(username);
    }

    public void disconnectUser(SocketChannel sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Socket must not be null!");
        }
        users.logoutUser(userSockets.get(sc));
        userSockets.remove(sc);
    }

    public void addSocketForUser(String username, SocketChannel sc) {
        if (username == null || sc == null) {
            throw new IllegalArgumentException("Arguments must not be null!");
        }
        userSockets.put(sc, username);
    }

    public String getUsernameBySocket(SocketChannel sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Socket must not be null!");
        }
        return userSockets.get(sc);
    }

    public List<String> getAllGiftReceivers() {
        return new ArrayList(wishList.keySet());
    }

    public List<String> getWishesFor(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null!");
        }
        return wishList.get(username);
    }

    public int getNumberOfRegisteredUsers() {
        return users.getNumberOfRegisteredUsers();
    }

    public int getNumberOfLoggedUsers() {
        return users.getNumberOfLoggedUsers();
    }

    public boolean isLogged(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null!");
        }
        return users.isLogged(username);
    }

    public boolean isRegistered(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null!");
        }
        return users.isRegistered(username);
    }

    public boolean hasWishesFor(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null!");
        }
        return wishList.containsKey(username);
    }

    public boolean wishIsAlreadySubmittedForUser(String username, String wish) {
        if (username == null || wish == null) {
            throw new IllegalArgumentException("Arguments must not be null!");
        }
        return wishList.containsKey(username)
                && wishList.get(username).contains(wish);
    }

    public boolean hasNoWishes() {
        return wishList.isEmpty();
    }

    public boolean isUsernameTaken(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null!");
        }
        return users.isUsernameTaken(username);
    }

    public boolean isCorrectUserPassCombination(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Arguments must not be null!");
        }
        return users.isCorrectUserPassCombination(username, password);
    }
}
