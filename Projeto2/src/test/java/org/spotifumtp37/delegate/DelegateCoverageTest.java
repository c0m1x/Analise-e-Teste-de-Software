package org.spotifumtp37.delegate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.spotifumtp37.model.SpotifUMData;
import org.spotifumtp37.model.album.Album;
import org.spotifumtp37.model.album.Song;
import org.spotifumtp37.model.playlist.Playlist;
import org.spotifumtp37.model.subscription.FreePlan;
import org.spotifumtp37.model.subscription.PremiumBase;
import org.spotifumtp37.model.subscription.PremiumTop;
import org.spotifumtp37.model.user.History;
import org.spotifumtp37.model.user.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class DelegateCoverageTest {

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void spotifumUiShouldRegisterAuthenticateAndRejectInvalidAdminLogin() throws Exception {
        System.setIn(new ByteArrayInputStream("admin\nwrong\n".getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        SpotifUMUI ui = new SpotifUMUI();

        assertTrue(ui.registerNewUser("user-1", "pw", "mail@mail.com", "street"));
        assertFalse(ui.registerNewUser("user-1", "pw", "mail@mail.com", "street"));
        assertTrue(ui.authenticateUser("user-1", "pw"));
        assertFalse(ui.authenticateUser("user-1", "bad"));
        assertFalse(ui.authenticateUser("missing", "pw"));

        ui.loginAsAdmin();
        assertTrue(out.toString().contains("Invalid admin credentials!"));
    }

    @Test
    void adminUiShouldPersistClearRestoreAndReportData() throws Exception {
        SpotifUMData data = buildData();
        Path jsonFile = Files.createTempFile("spotifum-admin", ".json");
        Path serFile = Files.createTempFile("spotifum-admin", ".ser");
        try {
            AdminUI adminUI = new AdminUI(data, scannerFor(jsonFile.toString() + System.lineSeparator()));
            adminUI.saveToJson(jsonFile.toString());
            adminUI.clearSystemData();
            assertFalse(data.existsUser("creator"));
            adminUI.loadFromJson(jsonFile.toString());
            assertTrue(data.existsUser("creator"));
            assertTrue(data.existsAlbum("album-1"));
            assertTrue(data.existsPlaylist("playlist-1"));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            adminUI.printCurrentData();
            assertTrue(out.toString().contains("Albums:"));

            AdminUI serialAdmin = new AdminUI(data, scannerFor(serFile.toString() + System.lineSeparator()));
            serialAdmin.serializeData();
            serialAdmin.clearSystemData();
            assertFalse(data.existsAlbum("album-1"));

            AdminUI deserializeAdmin = new AdminUI(data, scannerFor(serFile.toString() + System.lineSeparator()));
            deserializeAdmin.deserializeData();
            assertTrue(data.existsAlbum("album-1"));
            assertTrue(data.existsUser("creator"));
        } finally {
            Files.deleteIfExists(jsonFile);
            Files.deleteIfExists(serFile);
        }
    }

    @Test
    void adminUiShouldHandleDateQueries() throws Exception {
        SpotifUMData data = buildDataWithHistory();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        AdminUI invalidDateUI = new AdminUI(data, scannerFor("not-a-date\n"));
        invalidDateUI.handleTopListenerFromDate();
        assertTrue(out.toString().contains("Invalid date format"));

        out.reset();
        AdminUI validDateUI = new AdminUI(data, scannerFor("2026-03-01\n"));
        validDateUI.handleTopListenerFromDate();
        assertTrue(out.toString().contains("creator"));
    }

    @Test
    void userUiShouldCreateTopGenrePlaylistAndChangeSubscriptionPlans() throws Exception {
        SpotifUMData data = buildDataWithHistory();
        User loggedUser = data.getCurrentUserPointer("creator");
        UserUI userUI = new UserUI(data, loggedUser, scannerFor("top-genre-playlist\n"));
        userUI.setLoggedUser(loggedUser);

        invokePrivate(userUI, "createTopGenrePlaylist");
        assertTrue(data.existsPlaylist("top-genre-playlist"));

        invokePrivate(userUI, "alteraSubscriptionFreePlan");
        assertTrue(loggedUser.getSubscriptionPlan() instanceof FreePlan);

        invokePrivate(userUI, "alteraSubscriptionPremiumBasePlan");
        assertTrue(loggedUser.getSubscriptionPlan() instanceof PremiumBase);

        invokePrivate(userUI, "alteraSubscriptionPremiumTopPlan");
        assertTrue(loggedUser.getSubscriptionPlan() instanceof PremiumTop);
    }

    private void invokePrivate(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }

    private Scanner scannerFor(String text) {
        return new Scanner(new ByteArrayInputStream(text.getBytes()));
    }

    private SpotifUMData buildData() throws Exception {
        SpotifUMData data = new SpotifUMData();
        Song song = new Song("song-1", "artist", "publisher", "lyrics", "notes", "rock", 180);
        Album album = new Album("album-1", "artist", 2024, "rock", List.of(song));
        User creator = new User("creator", "creator@mail.com", "street", new PremiumBase(), "pw", 7, new ArrayList<History>());
        Playlist playlist = new Playlist(creator, "playlist-1", "desc", 0, "private", List.of(song));

        data.setMapAlbums(Map.of(album.getTitle(), album));
        data.setMapUsers(Map.of(creator.getName(), creator));
        data.setMapPlaylists(Map.of(playlist.getPlaylistName(), playlist));
        return data;
    }

    private SpotifUMData buildDataWithHistory() throws Exception {
        SpotifUMData data = new SpotifUMData();
        Song rock1 = new Song("rock-1", "artist-a", "publisher", "lyrics", "notes", "rock", 180);
        Song rock2 = new Song("rock-2", "artist-a", "publisher", "lyrics", "notes", "rock", 200);
        Song pop1 = new Song("pop-1", "artist-b", "publisher", "lyrics", "notes", "pop", 210);

        Album album = new Album("album-1", "artist-a", 2024, "mixed", List.of(rock1, rock2, pop1));
        User creator = new User("creator", "creator@mail.com", "street", new PremiumBase(), "pw", 7, new ArrayList<History>());
        creator.updateHistory(rock1);
        creator.updateHistory(rock2);
        creator.updateHistory(rock1);

        data.setMapAlbums(Map.of(album.getTitle(), album));
        data.setMapUsers(Map.of(creator.getName(), creator));
        return data;
    }
}