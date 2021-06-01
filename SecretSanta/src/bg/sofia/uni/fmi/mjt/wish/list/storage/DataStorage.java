package bg.sofia.uni.fmi.mjt.wish.list.storage;

import java.nio.channels.SocketChannel;
import java.util.List;

public interface DataStorage {
    List<String> removeWishesFor(String username);

    void addWish(String username, String wish);

    void addWishes(String username, List<String> wishes);

    void registerUser(String username, String password);

    void loginUser(String username);

    void logoutUser(String username);

    void disconnectUser(SocketChannel sc);

    void addSocketForUser(String username, SocketChannel sc);

    String getUsernameBySocket(SocketChannel sc);

    List<String> getAllGiftReceivers();

    List<String> getWishesFor(String username);

    int getNumberOfRegisteredUsers();

    int getNumberOfLoggedUsers();

    boolean isLogged(String username);

    boolean isRegistered(String username);

    boolean hasWishesFor(String username);

    boolean wishIsAlreadySubmittedForUser(String username, String wish);

    boolean hasNoWishes();

    boolean isUsernameTaken(String username);

    boolean isCorrectUserPassCombination(String username, String password);
}
