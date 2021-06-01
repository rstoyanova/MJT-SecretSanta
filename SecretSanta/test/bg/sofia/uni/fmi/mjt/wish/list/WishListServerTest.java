package bg.sofia.uni.fmi.mjt.wish.list;

import bg.sofia.uni.fmi.mjt.wish.list.storage.ServerData;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class WishListServerTest {
    private static SocketChannel testSocket;

    // error messages for different tests
    private final String errMessageGetWish =
            "GetWish doesn't return expected value";
    private final String errMessagePostWish =
            "Client command for register doesn't return expected value";
    private final String errMessageCommandReg =
            "Client command for register doesn't return expected value";
    private final String errMessageCommandLogin =
            "Client command for login doesn't return expected value";


    private final String correctUsername1 = "reni_s";
    private final String correctUsername2 = "boby.t";
    private final String correctPassword = "asd123";
    private final String space = " ";
    private final String filler = "alabala";
    private final String gift1 = "bike";


    // valid commands
    private final String commandForValidReg =
            "register" + space + correctUsername1 + space + correctPassword;
    private final String commandForValidLogin =
            "login" + space + correctUsername1 + space + correctPassword;

    private final CommandExecutor commandEx = new CommandExecutor();

    @Mock
    private ServerData serverDataMock;

    @InjectMocks
    private CommandExecutor commandExecutorMock;

    @BeforeClass
    public static void setUp() {
        try {
            testSocket = SocketChannel.open();
        } catch (IOException e) {
            System.out.println("Opening socket failed!\n");
            e.printStackTrace();
        }
    }

    @Test
    public void testGetWishWhenThereAreNon() {
        Mockito.when(serverDataMock.hasWishesFor(correctUsername1)).thenReturn(false);
        Mockito.when(serverDataMock.hasNoWishes()).thenReturn(true);

        final String EXPECTED_REPLY = "[ There are no students present in the wish list ]";
        String actualReply = commandExecutorMock.getWish(testSocket).trim();

        assertEquals(errMessageGetWish, EXPECTED_REPLY, actualReply);
    }

    @Test
    public void testGetWish() {
        final String gift2 = "ps5";
        List<String> giftReceivers = new ArrayList<>();
        giftReceivers.add(correctUsername1);
        List<String> wishes = new ArrayList<>();
        wishes.add(gift1);
        wishes.add(gift2);
        Mockito.when(serverDataMock.getUsernameBySocket(testSocket))
                .thenReturn(correctUsername2);
        Mockito.when(serverDataMock.getAllGiftReceivers())
                .thenReturn(giftReceivers);
        Mockito.when(serverDataMock.hasNoWishes())
                .thenReturn(false);
        Mockito.when((serverDataMock.hasWishesFor(correctUsername1)))
                .thenReturn(false);
        Mockito.when(serverDataMock.getWishesFor(correctUsername1))
                .thenReturn(wishes);

        final String EXPECTED_REPLY = "[ " + correctUsername1 + ": " + wishes + " ]";
        String actualReply = commandExecutorMock.getWish(testSocket).trim();

        assertEquals(errMessageGetWish, EXPECTED_REPLY, actualReply);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetWishWithNullArgument() {
        commandEx.getWish(null);
    }

    @Test
    public void testPostWishWithTooFewArgumentsInCommand() {
        final String command = "post-wish" + space + correctUsername1;
        final String EXPECTED_REPLY = "[ Invalid number of arguments ]";

        String actualReply = commandEx.postWish(command).trim();

        assertEquals(errMessagePostWish, EXPECTED_REPLY, actualReply);
    }

    @Test
    public void testPostWishForNotRegisteredUser() {
        final String command = "post-wish" + space + correctUsername1 + space + gift1;
        final String EXPECTED_REPLY = "[ Student with username " + correctUsername1
                + " is not registered ]";

        Mockito.when(serverDataMock.isRegistered(correctUsername1)).thenReturn(false);
        String actualReply = commandExecutorMock.postWish(command).trim();

        assertEquals(errMessagePostWish, EXPECTED_REPLY, actualReply);
    }

    @Test
    public void testPostWishWithAlreadyPostedWish() {
        final String command = "post-wish" + space + correctUsername1 + space + gift1;
        final String EXPECTED_REPLY = "[ The same gift for student " + correctUsername1
                + " was already submitted ]";

        Mockito.when(serverDataMock.isRegistered(correctUsername1))
                .thenReturn(true);
        Mockito.when((serverDataMock.wishIsAlreadySubmittedForUser(correctUsername1, gift1)))
                .thenReturn(true);
        String actualReply = commandExecutorMock.postWish(command).trim();

        assertEquals(errMessagePostWish, EXPECTED_REPLY, actualReply);
    }

    @Test
    public void testPostWish() {
        final String command = "post-wish" + space + correctUsername1 + space + gift1;
        final String EXPECTED_REPLY = "[ Gift " + gift1 + " for student "
                + correctUsername1 + " submitted successfully ]";

        Mockito.when(serverDataMock.isRegistered(correctUsername1))
                .thenReturn(true);
        Mockito.when((serverDataMock.wishIsAlreadySubmittedForUser(correctUsername1, gift1)))
                .thenReturn(false);
        String actualReply = commandExecutorMock.postWish(command).trim();

        assertEquals(errMessagePostWish, EXPECTED_REPLY, actualReply);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPostWishWithNullArgument() {
        commandEx.postWish(null);
    }

    @Test
    public void testClientCommandRegisterNewValidUser() {
        final String newUsername = "qwerty";
        final String command = "register" + space + newUsername + space + correctPassword;
        final String expectedReply = "[ Username " + newUsername + " successfully registered ]";
        final int EXPECTED_REGISTERED_USERS = commandEx.getNumberOfRegisteredUsers() + 1;
        String actualReply = commandEx.executeClientCommand(command, testSocket).trim();
        int actualRegisteredUsers = commandEx.getNumberOfRegisteredUsers();

        assertEquals(errMessageCommandReg, expectedReply, actualReply);
        assertEquals(EXPECTED_REGISTERED_USERS, actualRegisteredUsers);
    }

    @Test
    public void testClientCommandRegisterWithInvalidUsername() {
        final String incorrectUsername = "#reni";
        final String command = "register" + space + incorrectUsername + space + correctPassword;
        final String expectedReply = "[ Username " + incorrectUsername + " is invalid, select a valid one ]";
        final int EXPECTED_REGISTERED_USERS = commandEx.getNumberOfRegisteredUsers();
        String actualReply = commandEx.executeClientCommand(command, testSocket).trim();
        int actualRegisteredUsers = commandEx.getNumberOfRegisteredUsers();

        assertEquals(errMessageCommandReg, expectedReply, actualReply);
        assertEquals(EXPECTED_REGISTERED_USERS, actualRegisteredUsers);
    }

    @Test
    public void testClientCommandRegisterWithTooManyArguments() {
        final String command = "register" + space + correctUsername1 + space + correctPassword + space + filler;
        final String expectedReply = "[ Invalid number of arguments ]";
        final int EXPECTED_REGISTERED_USERS = commandEx.getNumberOfRegisteredUsers();
        String actualReply = commandEx.executeClientCommand(command, testSocket).trim();
        int actualRegisteredUsers = commandEx.getNumberOfRegisteredUsers();

        assertEquals(errMessageCommandReg, expectedReply, actualReply);
        assertEquals(EXPECTED_REGISTERED_USERS, actualRegisteredUsers);
    }

    @Test
    public void testClientCommandRegisterWithTooFewArguments() {
        final String command = "register" + space + correctUsername1;
        final String expectedReply = "[ Invalid number of arguments ]";
        final int EXPECTED_REGISTERED_USERS = commandEx.getNumberOfRegisteredUsers();
        String actualReply = commandEx.executeClientCommand(command, testSocket).trim();
        int actualRegisteredUsers = commandEx.getNumberOfRegisteredUsers();

        assertEquals(errMessageCommandReg, expectedReply, actualReply);
        assertEquals(EXPECTED_REGISTERED_USERS, actualRegisteredUsers);
    }

    @Test
    public void testClientCommandRegisterAutomaticLogin() {
        final int EXPECTED_LOGGED_USERS = commandEx.getNumberOfLoggedUsers() + 1;

        final String commandNewReg = "register" + space + correctUsername2 + space + correctPassword;
        commandEx.executeClientCommand(commandNewReg, testSocket);

        int actualLoggedUsers = commandEx.getNumberOfLoggedUsers();

        assertEquals("Number of logged users doesn't increase!",
                EXPECTED_LOGGED_USERS, actualLoggedUsers);
    }

    @Test
    public void testClientCommandRegisterWithAlreadyTakenUsername() {
        final String expectedReply = "[ Username " + correctUsername1 + " is already taken, select another one ]";
        commandEx.executeClientCommand(commandForValidReg, testSocket);
        String actualReply = commandEx.executeClientCommand(commandForValidReg, testSocket).trim();

        assertEquals(errMessageCommandReg, expectedReply, actualReply);
    }


    @Test
    public void testClientCommandLoginValidNotLoggedUser() {
        final String EXPECTED_REPLY = "[ User " + correctUsername1 + " successfully logged in ]";
        commandEx.executeClientCommand(commandForValidReg, testSocket);
        commandEx.executeClientCommand("logout", testSocket);
        String actualReply = commandEx.executeClientCommand(commandForValidLogin, testSocket).trim();

        assertEquals(errMessageCommandLogin, EXPECTED_REPLY, actualReply);
    }

    @Test
    public void testClientCommandLoginAlreadyLoggedUser() {
        final String EXPECTED_REPLY = "[ User " + correctUsername1 + " already logged ]";
        commandEx.executeClientCommand(commandForValidReg, testSocket);
        String actualReply = commandEx.executeClientCommand(commandForValidLogin, testSocket).trim();

        assertEquals(errMessageCommandLogin, EXPECTED_REPLY, actualReply);
    }

    @Test
    public void testClientCommandLoginWithTooFewArguments() {
        final String command = "login" + space + correctUsername1;
        final String EXPECTED_REPLY = "[ Invalid number of arguments ]";
        String actualReply = commandEx.executeClientCommand(command, testSocket).trim();

        assertEquals(errMessageCommandLogin, EXPECTED_REPLY, actualReply);
    }

    @Test
    public void testClientCommandLoginWithTooManyArguments() {
        final String command = commandForValidLogin + space + filler;
        final String EXPECTED_REPLY = "[ Invalid number of arguments ]";
        String actualReply = commandEx.executeClientCommand(command, testSocket).trim();

        assertEquals(errMessageCommandLogin, EXPECTED_REPLY, actualReply);
    }

    @Test
    public void testClientCommandDisconnect() {
        final String command = "disconnect";
        final String EXPECTED_REPLY = "[ Disconnected from server ]";
        final String errMessage =
                "Client command with disconnect doesn't return expected value";

        commandEx.executeClientCommand(commandForValidReg, testSocket);
        String actualReply = commandEx.executeClientCommand(command, testSocket).trim();
        assertEquals(errMessage, EXPECTED_REPLY, actualReply);
    }

    @Test
    public void testClientCommandGetWishFromNotLoggedUser() {
        commandEx.executeClientCommand(commandForValidReg, testSocket);
        commandEx.executeClientCommand("logout", testSocket);

        final String command = "get-wish";

        final String EXPECTED_REPLY = "[ You are not logged in ]";
        final String actualReply = commandEx.executeClientCommand(command, testSocket).trim();
        final String errMessage =
                "ClientCommand doesn't return expected value for not logged users";
        assertEquals(errMessage, EXPECTED_REPLY, actualReply);
    }

    @Test
    public void testClientCommandPostWishFromNotLoggedUser() {
        commandEx.executeClientCommand(commandForValidReg, testSocket);
        commandEx.executeClientCommand("logout", testSocket);

        final String command = "post-wish" + space + correctUsername1 + space + gift1;

        final String EXPECTED_REPLY = "[ You are not logged in ]";
        final String actualReply = commandEx.executeClientCommand(command, testSocket).trim();
        final String errMessage =
                "ClientCommand doesn't return expected value for not logged users";
        assertEquals(errMessage, EXPECTED_REPLY, actualReply);
    }

    @Test
    public void testClientCommandLogoutFromNotLoggedUser() {
        commandEx.executeClientCommand(commandForValidReg, testSocket);
        commandEx.executeClientCommand("logout", testSocket);

        final String command = "logout";

        final String EXPECTED_REPLY = "[ You are not logged in ]";
        final String actualReply = commandEx.executeClientCommand(command, testSocket).trim();
        final String errMessage =
                "ClientCommand doesn't return expected value for not logged users";
        assertEquals(errMessage, EXPECTED_REPLY, actualReply);
    }

    @Test
    public void testClientCommandWithUnknownCommand() {
        final String command = "get-all-wishes";

        final String EXPECTED_REPLY = "[ Unknown command ]";
        final String actualReply = commandEx.executeClientCommand(command, testSocket).trim();
        final String errMessage =
                "ClientCommand doesn't return expected value for an unknown command";
        assertEquals(errMessage, EXPECTED_REPLY, actualReply);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClientCommandWithNullSocket() {
        commandEx.executeClientCommand("", null);
    }

    @AfterClass
    public static void cleanUp() {
        try {
            testSocket.close();
        } catch (IOException e) {
            System.out.println("Closing socket failed!" + System.lineSeparator());
        }
    }
}

