package org.Controller;

import org.Exceptions.AlreadyExistsException;
import org.Exceptions.NoUsersInDatabaseException;
import org.Model.SpotifUM;
import org.Model.User.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class ControllerMutationCoverageTest {

    private static class ThrowingSpotifUM extends SpotifUM {
        @Override
        public void addNewMusic(String musicName, String artistName, String publisher, String lyrics,
                                String musicalFigures, String genre, String albumName, int duration,
                                boolean explicit, String url) throws AlreadyExistsException {
            throw new AlreadyExistsException(musicName);
        }

        @Override
        public void addNewAlbum(String albumName, String autor) {
            throw new RuntimeException("boom");
        }

        @Override
        public String listCurrentUserPlaylists() {
            throw new RuntimeException("boom");
        }

        @Override
        public String listPublicPlaylists() {
            throw new RuntimeException("boom");
        }

        @Override
        public String listAllMusics() {
            throw new RuntimeException("boom");
        }

        @Override
        public String listAllAlbums() {
            throw new RuntimeException("boom");
        }

        @Override
        public String listAllGenres() {
            throw new RuntimeException("boom");
        }

        @Override
        public int getPublicPlaylistSize() {
            throw new RuntimeException("boom");
        }
    }

    private static class EmptyDateStatsSpotifUM extends SpotifUM {
        @Override
        public User getUserWithMostReproductions(LocalDate startDate, LocalDate endDate) throws NoUsersInDatabaseException {
            return new User("empty", "empty@mail.com", "addr", "pw");
        }
    }

    private static Controller seededController() {
        SpotifUM model = new SpotifUM();
        model.populateDatabase();
        Controller controller = new Controller(model);
        controller.addNewUser("u", "u@mail.com", "addr", "pw");
        assertTrue(controller.loginWithMessage("u", "pw"));
        return controller;
    }

    private static int firstPlaylistId(String listing) {
        Matcher matcher = Pattern.compile("\\[#(\\d+)\\]").matcher(listing);
        assertTrue(matcher.find(), listing);
        return Integer.parseInt(matcher.group(1));
    }

    @Test
    void profilePlanAndPermissionMethodsExposeActualStateChanges() {
        Controller controller = seededController();

        assertFalse(controller.canCurrentUserSkip());
        assertFalse(controller.canCurrentUserChooseWhatToPlay());
        assertFalse(controller.currentUserAccessToFavorites());

        assertTrue(controller.changeCurrentUserEmail("new@mail.com").startsWith("✅"));
        assertEquals("new@mail.com", controller.getSpotifUM().getCurrentUser().getEmail());

        assertTrue(controller.changeCurrentUserUserName("renamed").startsWith("✅"));
        assertEquals("renamed", controller.getSpotifUM().getCurrentUser().getUsername());
        assertTrue(controller.getSpotifUM().userExists("renamed"));
        assertFalse(controller.getSpotifUM().userExists("u"));

        assertTrue(controller.setPremiumBasePlan().startsWith("✨"));
        assertEquals("PremiumBase", controller.getCurrentUserPlan());
        assertTrue(controller.canCurrentUserSkip());
        assertTrue(controller.canCurrentUserChooseWhatToPlay());
        assertFalse(controller.currentUserAccessToFavorites());

        assertTrue(controller.setPremiumTopPlan().startsWith("👑"));
        assertEquals("PremiumTop", controller.getCurrentUserPlan());
        assertTrue(controller.currentUserAccessToFavorites());

        assertTrue(controller.setFreePlan().startsWith("✅"));
        assertEquals("Free", controller.getCurrentUserPlan());
        assertFalse(controller.canCurrentUserSkip());
    }

    @Test
    void listAndCreateMethodsReturnNonEmptyMessagesInBothBranches() {
        Controller empty = new Controller(new SpotifUM());
        empty.addNewUser("emptyUser", "empty@mail.com", "addr", "pw");
        assertTrue(empty.loginWithMessage("emptyUser", "pw"));
        empty.setPremiumBasePlan();
        assertEquals("📭 Não existem músicas disponíveis.", empty.listAllMusics());
        assertEquals("📭 Não existem álbuns disponíveis.", empty.listAllAlbums());
        assertEquals("📭 Não existem géneros disponíveis.", empty.listAllGenres());
        assertEquals("📭 Não existem playlists públicas.", empty.listPublicPlaylists());
        assertEquals("📭 Não existem playlists.", empty.listUserPlaylists());
        assertTrue(empty.getPublicPlaylistCount().contains("0"));

        Controller controller = seededController();
        assertTrue(controller.createAlbum("album-extra", "artist-extra").contains("album-extra"));
        assertTrue(controller.addMusic("song-extra", "artist-extra", "pub", "lyrics", "notes", "rock", "album-extra", 100, false, null)
                .contains("song-extra"));
        assertTrue(controller.listAllMusics().contains("song-extra"));
        assertTrue(controller.listAllAlbums().contains("album-extra"));
        assertTrue(controller.listAllGenres().contains("rock") || controller.listAllGenres().contains("Rock"));
    }

    @Test
    void playlistControllerMethodsReturnNamesAndMutateLibrary() {
        Controller controller = seededController();
        controller.setPremiumTopPlan();

        assertTrue(controller.addToCurrentUserPlaylists("mine").startsWith("✅"));
        String listing = controller.listUserPlaylists();
        assertTrue(listing.contains("mine"));
        int playlistId = firstPlaylistId(listing);

        assertEquals("mine", controller.getPlaylistId(playlistId));
        assertTrue(controller.addMusicToCurrentUserPlaylist(playlistId, "Shape of You").contains("mine"));
        assertTrue(controller.listPlaylistMusics(playlistId).contains("Shape of You"));
        assertTrue(controller.getPlaylistMusicNames(playlistId).orElseThrow().contains("Shape of You"));

        assertTrue(controller.setPlaylistAsPublic(playlistId).contains("mine"));
        assertTrue(controller.listPublicPlaylists().contains("mine"));
        assertTrue(controller.getRandomPlaylistMusicNames().isPresent());
        assertFalse(controller.getRandomPlaylistMusicNames().orElseThrow().isEmpty());
    }

    @Test
    void rankingMessagesDifferentiateEmptyAndPopulatedUsers() {
        Controller controller = seededController();
        controller.setPremiumTopPlan();
        controller.playMusic("Shape of You");
        assertTrue(controller.addToCurrentUserPlaylists("mine").startsWith("✅"));
        assertTrue(controller.addToCurrentUserPlaylists("mine-2").startsWith("✅"));
        assertTrue(controller.addToCurrentUserPlaylists("mine-3").startsWith("✅"));

        String points = controller.getUserWithMostPoints();
        String playlists = controller.getUserWithMostPlaylists();
        String reproductions = controller.getUserWithMostReproductions();

        assertTrue(points.contains("u") && points.contains("pontos"));
        assertFalse(playlists.isEmpty());
        assertFalse(playlists.startsWith("❌"));
        assertTrue(reproductions.contains("u") && reproductions.contains("reproduções"));

        SpotifUM onlyUsers = new SpotifUM();
        onlyUsers.addNewUser("noActivity", "n@mail.com", "addr", "pw");
        Controller emptyActivity = new Controller(onlyUsers);
        assertEquals("Não existe utilizadores com playlists.", emptyActivity.getUserWithMostPlaylists());
        assertEquals("Não existe utilizadores com reproduções.", emptyActivity.getUserWithMostReproductions());
    }

    @Test
    void controllerErrorBranchesReturnVisibleMessages() {
        Controller throwing = new Controller(new ThrowingSpotifUM());

        assertTrue(throwing.addMusic("song", "artist", "pub", "lyrics", "notes", "rock", "album", 100, false, null)
                .startsWith("❌ Erro ao adicionar a música:"));
        assertTrue(throwing.createAlbum("album", "artist").startsWith("❌ Erro ao criar o álbum:"));
        assertTrue(throwing.listUserPlaylists().startsWith("❌ Erro ao listar playlists:"));
        assertTrue(throwing.listPublicPlaylists().startsWith("❌ Erro ao listar playlists:"));
        assertTrue(throwing.listAllMusics().startsWith("❌ Erro ao listar músicas:"));
        assertTrue(throwing.listAllAlbums().startsWith("❌ Erro ao listar álbuns:"));
        assertTrue(throwing.listAllGenres().startsWith("❌ Erro ao listar géneros:"));
        assertTrue(throwing.getPublicPlaylistCount().startsWith("❌ Erro ao obter o número de playlists públicas:"));

        Controller controller = seededController();
        assertTrue(controller.changeCurrentUserUserName("u").startsWith("❌ Erro ao alterar o nome de utilizador:"));
        assertTrue(controller.addMusicToCurrentUserPlaylist(-1, "Shape of You")
                .startsWith("❌ Erro ao adicionar a música à playlist:"));
        assertFalse(controller.listPlaylistMusics(-1).isEmpty());
        assertTrue(controller.getPlaylistId(-1).startsWith("❌ Erro ao obter o nome da playlist:"));
    }

    @Test
    void dateBasedTopListenerMessageIncludesUserAndCount() {
        Controller controller = seededController();
        controller.setPremiumTopPlan();
        controller.playMusic("Shape of You");

        String message = controller.getUserWithMostReproductions(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));

        assertTrue(message.contains("u"), message);
        assertTrue(message.contains("1 reprodu"), message);
    }

    @Test
    void dateBasedTopListenerMessageHandlesReturnedUserWithoutHistory() {
        Controller controller = new Controller(new EmptyDateStatsSpotifUM());

        assertEquals("Não existe utilizadores com reproduções.",
                controller.getUserWithMostReproductions(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)));
    }
}
