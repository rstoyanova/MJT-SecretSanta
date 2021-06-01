package bg.sofia.uni.fmi.mjt.wish.list.user;

public interface UserCollection {
    void registerUser(String username, String password);

    void loginUser(String username);

    void logoutUser(String username);

    boolean isRegistered(String username);

    boolean isCorrectUserPassCombination(String username, String password);

    boolean isUsernameTaken(String username);

    boolean isLogged(String username);

    int getNumberOfLoggedUsers();

    int getNumberOfRegisteredUsers();
}
