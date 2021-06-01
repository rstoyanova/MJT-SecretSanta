package bg.sofia.uni.fmi.mjt.wish.list;

import bg.sofia.uni.fmi.mjt.wish.list.storage.ServerData;
import bg.sofia.uni.fmi.mjt.wish.list.user.Users;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommandExecutor {
    private static final String NL = System.lineSeparator();
    private final ServerData data;

    CommandExecutor() {
        data = new ServerData();
    }

    CommandExecutor(ServerData data) {
        this.data = data;
    }

    public String executeClientCommand(String command, SocketChannel sc) {
        if (command == null || sc == null) {
            throw new IllegalArgumentException("Arguments must not be null!" + NL);
        } else if (command.startsWith("register")) {
            return register(command, sc);
        } else if (command.startsWith("login")) {
            return login(command, sc);
        } else if (command.equals("disconnect")) {
            return disconnect(sc);
        } else if (!data.isLogged(data.getUsernameBySocket(sc))
                && (command.equals("get-wish")
                || command.startsWith("post-wish")
                || command.equals("logout"))) {
            return "[ You are not logged in ]" + NL;
        } else if (command.equals("get-wish")) {
            return getWish(sc);
        } else if (command.startsWith("post-wish")) {
            return postWish(command);
        } else if (command.equals("logout")) {
            return logout(sc);
        } else {
            return "[ Unknown command ]" + NL;
        }
    }

    public String getWish(SocketChannel sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Socket must not be null!" + NL);
        }
        String username = data.getUsernameBySocket(sc);
        List<String> wishes = new ArrayList<>();
        if (data.hasWishesFor(username)) {
            wishes = data.removeWishesFor(username);
        }
        if (data.hasNoWishes()) {
            return "[ There are no students present in the wish list ]" + NL;
        } else {
            List<String> keys = data.getAllGiftReceivers();
            String randomKey = keys.get(new Random().nextInt(keys.size()));
            List<String> wishesToReturn = data.getWishesFor(randomKey);
            if (!wishes.isEmpty()) {
                data.addWishes(username, wishes);
            }
            data.removeWishesFor(username);
            return "[ " + randomKey + ": " + wishesToReturn + " ]" + NL;
        }
    }

    public String postWish(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message must not be null!" + NL);
        }
        String[] tokens = message.split(" ", 3);
        if (tokens.length < 3) {
            return "[ Invalid number of arguments ]" + NL;
        }
        String toUsername = tokens[1];
        String wish = tokens[2];

        if (!data.isRegistered(toUsername)) {
            return "[ Student with username " + toUsername
                    + " is not registered ]" + NL;
        } else if (data.wishIsAlreadySubmittedForUser(toUsername, wish)) {
            return "[ The same gift for student " + toUsername
                    + " was already submitted ]" + NL;
        } else {
            data.addWish(toUsername, wish);
            return "[ Gift " + wish + " for student "
                    + toUsername + " submitted successfully ]" + NL;
        }
    }


    private String register(String message, SocketChannel sc) {
        if (message == null || sc == null) {
            throw new IllegalArgumentException("Arguments must not be null!" + NL);
        }
        String[] tokens = message.split(" ");
        if (tokens.length != 3) {
            return "[ Invalid number of arguments ]" + NL;
        }
        String username = tokens[1];
        String password = tokens[2];

        if (!Users.isValidUsername(username)) {
            return "[ Username " + username + " is invalid, select a valid one ]" + NL;
        } else if (data.isUsernameTaken(username)) {
            return "[ Username " + username + " is already taken, select another one ]" + NL;
        } else {
            data.registerUser(username, password);
            data.addSocketForUser(username, sc);
            return "[ Username " + username + " successfully registered ]" + NL;
        }
    }

    private String login(String message, SocketChannel sc) {
        if (message == null || sc == null) {
            throw new IllegalArgumentException("Arguments must not be null!" + NL);
        }
        String[] tokens = message.split(" ");
        if (tokens.length != 3) {
            return "[ Invalid number of arguments ]" + NL;
        }
        String username = tokens[1];
        String password = tokens[2];

        if (data.isLogged(username)) {
            return "[ User " + username + " already logged ]" + NL;
        } else if (!data.isCorrectUserPassCombination(username, password)) {
            return "[ Invalid username/password combination ]" + NL;
        } else {
            data.addSocketForUser(username, sc);
            data.loginUser(username);
            return "[ User " + username + " successfully logged in ]" + NL;
        }
    }

    private String logout(SocketChannel sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Socket must not be null!" + NL);
        }
        String username = data.getUsernameBySocket(sc);
        data.logoutUser(username);
        return "[ Successfully logged out ]" + NL;
    }

    public int getNumberOfRegisteredUsers() {
        return data.getNumberOfRegisteredUsers();
    }

    public int getNumberOfLoggedUsers() {
        return data.getNumberOfLoggedUsers();
    }

    private String disconnect(SocketChannel sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Socket must not be null!" + NL);
        }
        data.logoutUser(data.getUsernameBySocket(sc));
        data.disconnectUser(sc);
        return "[ Disconnected from server ]" + NL;
    }
}
