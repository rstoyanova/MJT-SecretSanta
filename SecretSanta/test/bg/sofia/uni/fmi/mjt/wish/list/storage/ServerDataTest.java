package bg.sofia.uni.fmi.mjt.wish.list.storage;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ServerDataTest {
    private ServerData testServerData;

    final String username1 = "reni_s";
    final String username2 = "boby";
    final String username3 = "pesho_97";

    final String wish1 = "bike";
    final String wish2 = "ps5";
    final String wish3 = "lol skin";
    final String wish4 = "t-shirt";

    final String pass = "asd123";

    @Before
    public void setUp() {
        testServerData = new ServerData();
        testServerData.registerUser(username1, pass);
        testServerData.registerUser(username2, pass);
        testServerData.registerUser(username3, pass);


        testServerData.addWish(username1, wish1);
        testServerData.addWish(username2, wish2);
        testServerData.addWish(username2, wish3);
        testServerData.addWish(username3, wish4);
    }

    @Test
    public void testRemoveWishesForUser() {
        final List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add(wish2);
        expectedOutput.add(wish3);
        List<String> actualOutput = testServerData.removeWishesFor(username2);

        assertEquals(expectedOutput, actualOutput);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveWishesForWithNullUsername() {
        testServerData.removeWishesFor(null);
    }

    @Test
    public void testAddWishForNewUser() {
        final String newUsername = "newUser";
        assertFalse(testServerData.hasWishesFor(newUsername));

        testServerData.addWish(newUsername, wish1);
        assertTrue(testServerData.hasWishesFor(newUsername));
    }

    @Test
    public void testAddWishForUserInTheSystem() {
        assertTrue(testServerData.hasWishesFor(username1));
        final String wish = "cook book";

        assertFalse(testServerData.wishIsAlreadySubmittedForUser(username1, wish));
        testServerData.addWish(username1, wish);
        assertTrue(testServerData.wishIsAlreadySubmittedForUser(username1, wish));
    }

    /*
    @Test
    public void testGetAllGiftReceivers() {
        List<String> expectedUsername = new ArrayList<>();
        expectedUsername.add(username1);
        expectedUsername.add(username2);
        expectedUsername.add(username3);

        List<String> actualUsernames = testServerData.getAllGiftReceivers();

        assertEquals(expectedUsername, actualUsernames);
    }

     */

    @Test
    public void testGetWishesFor() {
        List<String> expectedWishes = new ArrayList<>();
        expectedWishes.add(wish2);
        expectedWishes.add(wish3);

        List<String> actualWishes = testServerData.getWishesFor(username2);
        assertEquals(expectedWishes, actualWishes);
    }

    @Test
    public void testIsRegistered() {
        assertTrue(testServerData.isRegistered(username1));
        assertTrue(testServerData.isRegistered(username2));
        assertTrue(testServerData.isRegistered(username3));

        final String randomUsername = "alabala";
        assertFalse(testServerData.isRegistered(randomUsername));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddWishWithNullUsername() {
        testServerData.addWish(null, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddWishWithNullWish() {
        testServerData.addWish("", null);
    }
}
