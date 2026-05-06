package org.spotifumtp37.delegate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.spotifumtp37.model.SpotifUMData;
import org.spotifumtp37.model.album.Album;
import org.spotifumtp37.model.album.ExplicitSong;
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
import java.lang.reflect.Field;
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

    @Test
    void userUiShouldChangePasswordWithValidationLoops() {
        SpotifUMData data = new SpotifUMData();
        User user = new User("u", "u@mail.com", "street", new PremiumBase(), "oldpw", 0, new ArrayList<History>());
        UserUI userUI = new UserUI(data, user, scannerFor("\nwrong\noldpw\nnew1\nnew2\nnewpw\nnewpw\n"));

        userUI.changePassword();

        assertEquals("newpw", user.getPassword());
    }

    @Test
    void userUiShouldChangeEmailOnlyWhenValid() {
        SpotifUMData data = new SpotifUMData();
        User user = new User("u", "old@mail.com", "street", new PremiumBase(), "pw", 0, new ArrayList<History>());
        UserUI userUI = new UserUI(data, user, scannerFor("\nwrong@mail.com\nold@mail.com\ninvalid-email\nnew@mail.com\n"));

        userUI.changeEmail();

        assertEquals("new@mail.com", user.getEmail());
    }

    @Test
    void userUiShouldChangeAddressOnlyWhenCurrentMatches() {
        SpotifUMData data = new SpotifUMData();
        User user = new User("u", "u@mail.com", "old street", new PremiumBase(), "pw", 0, new ArrayList<History>());
        UserUI userUI = new UserUI(data, user, scannerFor("\nwrong address\nold street\n\nnew street\n"));

        userUI.changeAddress();

        assertEquals("new street", user.getAddress());
    }

    @Test
    void userUiShouldCreatePlaylistAndPersistIt() {
        SpotifUMData data = new SpotifUMData();
        Song song = new Song("song-1", "artist", "publisher", "lyrics", "notes", "rock", 180);
        Album album = new Album("album-1", "artist", 2024, "rock", List.of(song));
        User creator = new User("creator", "creator@mail.com", "street", new PremiumBase(), "pw", 0, new ArrayList<History>());
        data.setMapAlbums(Map.of(album.getTitle(), album));

        UserUI userUI = new UserUI(data, creator, scannerFor("\nplaylist-created\ndesc\nprivate\n1\nalbum-1\nsong-1\n"));

        userUI.createPlaylist();

        assertTrue(data.existsPlaylist("playlist-created"));
        try {
            Playlist created = data.getPlaylist("playlist-created");
            assertEquals(1, created.getSongs().size());
            assertEquals("song-1", created.getSongs().get(0).getName());
        } catch (Exception e) {
            fail("Playlist should exist after creation", e);
        }
    }

    @Test
    void userUiShouldAddSongToExistingOwnPlaylist() throws Exception {
        SpotifUMData data = new SpotifUMData();
        Song song = new Song("song-1", "artist", "publisher", "lyrics", "notes", "rock", 180);
        Album album = new Album("album-1", "artist", 2024, "rock", List.of(song));
        User creator = new User("creator", "creator@mail.com", "street", new PremiumBase(), "pw", 0, new ArrayList<History>());
        Playlist playlist = new Playlist(creator, "playlist-1", "desc", 0, "private", new ArrayList<>());
        data.setMapAlbums(Map.of(album.getTitle(), album));
        data.setMapPlaylists(Map.of(playlist.getPlaylistName(), playlist));

        UserUI userUI = new UserUI(data, creator, scannerFor("\nplaylist-1\nalbum-1\nsong-1\n"));

        invokePrivate(userUI, "addSongToPlaylist");

        Playlist updated = data.getPlaylist("playlist-1");
        assertEquals(1, updated.getSongs().size());
        assertEquals("song-1", updated.getSongs().get(0).getName());
    }

    @Test
    void userUiShouldDeleteOwnPlaylist() {
        SpotifUMData data = new SpotifUMData();
        User creator = new User("creator", "creator@mail.com", "street", new PremiumBase(), "pw", 0, new ArrayList<History>());
        Playlist playlist = new Playlist(creator, "playlist-to-delete", "desc", 0, "private", new ArrayList<>());
        data.setMapPlaylists(Map.of(playlist.getPlaylistName(), playlist));

        UserUI userUI = new UserUI(data, creator, scannerFor("\nplaylist-to-delete\n"));

        userUI.deletePlaylist();

        assertFalse(data.existsPlaylist("playlist-to-delete"));
    }

    @Test
    void userUiShowChangeSubscriptionMenuShouldExecuteSelectedHandler() throws Exception {
        SpotifUMData data = new SpotifUMData();
        User user = new User("creator", "creator@mail.com", "street", new FreePlan(), "pw", 0, new ArrayList<History>());
        UserUI userUI = new UserUI(data, user, scannerFor(""));

        setNewMenuInput("2\n0\n");
        invokePrivate(userUI, "showChangeSubscriptionMenu");

        assertTrue(user.getSubscriptionPlan() instanceof PremiumBase);
    }

    @Test
    void userUiShowUserEditMenuShouldDispatchToChangeEmail() throws Exception {
        SpotifUMData data = new SpotifUMData();
        User user = new User("creator", "old@mail.com", "street", new PremiumBase(), "pw", 0, new ArrayList<History>());
        UserUI userUI = new UserUI(data, user, scannerFor("\nold@mail.com\nnew@mail.com\n"));

        setNewMenuInput("2\n0\n");
        invokePrivate(userUI, "showUserEditMenu");

        assertEquals("new@mail.com", user.getEmail());
    }

    @Test
    void userUiShowUserPlaylistManagementMenuShouldDispatchToCreatePlaylist() throws Exception {
        SpotifUMData data = new SpotifUMData();
        Song song = new Song("song-1", "artist", "publisher", "lyrics", "notes", "rock", 180);
        Album album = new Album("album-1", "artist", 2024, "rock", List.of(song));
        User user = new User("creator", "creator@mail.com", "street", new PremiumBase(), "pw", 0, new ArrayList<History>());
        data.setMapAlbums(Map.of(album.getTitle(), album));
        UserUI userUI = new UserUI(data, user, scannerFor("\nfrom-menu\ndesc\npublic\n1\nalbum-1\nsong-1\n"));

        setNewMenuInput("1\n0\n");
        userUI.showUserPlaylistManagementMenu();

        assertTrue(data.existsPlaylist("from-menu"));
    }

    @Test
    void userUiShouldCreateTopGenrePlaylistWithMaxTime() throws Exception {
        SpotifUMData data = buildDataWithHistory();
        User user = data.getCurrentUserPointer("creator");
        UserUI userUI = new UserUI(data, user, scannerFor("5\ntime-playlist\n"));

        invokePrivate(userUI, "createTopGenrePlaylistWithMaxTime");

        assertTrue(data.existsPlaylist("time-playlist"));
    }

    @Test
    void userUiShouldCreateTopGenrePlaylistWithExplicitOnly() throws Exception {
        SpotifUMData data = new SpotifUMData();
        Song explicitRock = new ExplicitSong("explicit-rock", "artist", "publisher", "lyrics", "notes", "rock", 180);
        Song explicitRock2 = new ExplicitSong("explicit-rock-2", "artist", "publisher", "lyrics", "notes", "rock", 200);
        Song cleanPop = new Song("clean-pop", "artist", "publisher", "lyrics", "notes", "pop", 180);
        Album album = new Album("album-explicit", "artist", 2024, "mixed", List.of(explicitRock, explicitRock2, cleanPop));
        User user = new User("creator", "creator@mail.com", "street", new PremiumTop(), "pw", 0, new ArrayList<History>());
        user.updateHistory(explicitRock);
        user.updateHistory(explicitRock2);
        user.updateHistory(explicitRock);
        data.setMapAlbums(Map.of(album.getTitle(), album));
        data.setMapUsers(Map.of(user.getName(), user));
        UserUI userUI = new UserUI(data, user, scannerFor("explicit-playlist\n"));

        invokePrivate(userUI, "createTopGenrePlaylistWithExplicitOnly");

        assertTrue(data.existsPlaylist("explicit-playlist"));
        Playlist created = data.getPlaylist("explicit-playlist");
        assertFalse(created.getSongs().isEmpty());
        assertTrue(created.getSongs().stream().allMatch(s -> s instanceof ExplicitSong));
    }

    private void invokePrivate(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }

    private void setNewMenuInput(String text) throws Exception {
        Field scannerField = NewMenu.class.getDeclaredField("is");
        scannerField.setAccessible(true);
        scannerField.set(null, new Scanner(new ByteArrayInputStream(text.getBytes())));
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