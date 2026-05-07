package org.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.Controller.dtos.MusicInfo;
import org.Model.SpotifUM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ControllerAdditionalCoverageTest {

    private static int firstPlaylistIdFromListing(String listing) {
        Pattern pattern = Pattern.compile("\\[#(\\d+)\\]");
        Matcher matcher = pattern.matcher(listing);
        assertTrue(matcher.find(), "Expected a playlist id in listing, got: " + listing);
        return Integer.parseInt(matcher.group(1));
    }

    @Test
    void freeUser_libraryActionsReturnErrorMessages() {
        Controller controller = new Controller(new SpotifUM());
        controller.addNewUser("freeUser", "f@x.com", "addr", "pw");
        assertTrue(controller.loginWithMessage("freeUser", "pw"));

        assertTrue(controller.listUserPlaylists().contains("Erro") || controller.listUserPlaylists().startsWith("❌"));
        assertTrue(controller.addToCurrentUserPlaylists("MyPlaylist").contains("Erro") || controller.addToCurrentUserPlaylists("MyPlaylist").startsWith("❌"));
    }

    @Test
    void premiumUser_exercisesPlaylistsReproductionAndStatsFlows() {
        SpotifUM model = new SpotifUM();
        model.populateDatabase();
        Controller controller = new Controller(model);

        // Before any plays: stats should error
        assertTrue(controller.getMostReproducedArtist().startsWith("❌"));
        assertTrue(controller.getMostReproducedGenre().startsWith("❌"));

        // Create user and upgrade plan to unlock library/permissions
        controller.addNewUser("u1", "u1@x.com", "addr", "pw");
        assertTrue(controller.loginWithMessage("u1", "pw"));
        controller.setPremiumTopPlan();

        // Ensure content exists (covers list methods)
        assertTrue(controller.listAllMusics().contains("🎵") || controller.listAllMusics().contains("🎶"));
        assertTrue(controller.listAllAlbums().contains("Lista") || controller.listAllAlbums().contains("📀"));
        assertTrue(controller.listAllGenres().contains("Lista") || controller.listAllGenres().contains("•"));

        // Playlist creation and manipulation
        assertTrue(controller.addToCurrentUserPlaylists("P1").startsWith("✅"));
        String playlistsListing = controller.listUserPlaylists();
        assertTrue(playlistsListing.contains("P1"));
        int playlistId = firstPlaylistIdFromListing(playlistsListing);

        // Add existing music to playlist (from populateDatabase)
        String addMusicMsg = controller.addMusicToCurrentUserPlaylist(playlistId, "Shape of You");
        assertTrue(addMusicMsg.startsWith("✅"), addMusicMsg);
        assertTrue(controller.listPlaylistMusics(playlistId).contains("Shape of You"));

        Optional<List<String>> playlistMusicNames = controller.getPlaylistMusicNames(playlistId);
        assertTrue(playlistMusicNames.isPresent());
        assertTrue(playlistMusicNames.get().contains("Shape of You"));

        // Remove and verify error branch on removing again
        assertTrue(controller.removeMusicFromPlaylist("Shape of You", playlistId).startsWith("✅"));
        assertTrue(controller.removeMusicFromPlaylist("Shape of You", playlistId).startsWith("❌"));

        // Publish playlist and add it to another user's library
        assertTrue(controller.setPlaylistAsPublic(playlistId).startsWith("✅"));
        assertTrue(controller.setPlaylistAsPublic(playlistId).startsWith("❌"));
        assertTrue(controller.listPublicPlaylists().contains("P1"));
        assertTrue(controller.getPublicPlaylistCount().matches(".*\\d+.*"));

        controller.addNewUser("u2", "u2@x.com", "addr", "pw");
        assertTrue(controller.loginWithMessage("u2", "pw"));
        controller.setPremiumBasePlan();
        assertTrue(controller.addPublicPlaylistToLibrary(playlistId).startsWith("✅"));
        assertTrue(controller.addPublicPlaylistToLibrary(playlistId).startsWith("❌"));

        // Upgrade again to PremiumTop to ensure all permissions are enabled
        controller.setPremiumTopPlan();

        // Add a multimedia explicit song to cover the URL branch
        controller.createAlbum("ExtraAlbum", "ExtraArtist");
        assertTrue(controller.addMusic(
                "ExplicitMM",
                "ExtraArtist",
                "Pub",
                "la la la",
                "fig",
                "Pop",
                "ExtraAlbum",
                120,
                true,
                "http://example.com"
        ).startsWith("✅"));

        // Music playback (success + failure) + favorite playlist path
        MusicInfo played = controller.playMusic("ExplicitMM");
        assertEquals("ExplicitMM", played.getMusicName());
        assertTrue(played.isExplicit());
        assertNotNull(played.getLyrics());

        MusicInfo missing = controller.playMusic("does-not-exist");
        assertNotNull(missing.getErrorMessage());
        assertFalse(missing.getErrorMessage().isEmpty());

        Optional<List<String>> favorites = controller.getFavoritePlaylistMusicNames(true, 10_000);
        assertTrue(favorites.isPresent());
        assertTrue(favorites.get().contains("ExplicitMM"));

        // Genre playlist success + failure
        assertTrue(controller.createGenrePlaylist("PopMix", "Pop", 10_000).startsWith("✅"));
        assertTrue(controller.createGenrePlaylist("Nope", "NON_EXISTENT_GENRE", 10_000).startsWith("❌"));

        // Plan permissions (covers canSkip/choose/favorites)
        assertTrue(controller.canCurrentUserSkip());
        assertTrue(controller.canCurrentUserChooseWhatToPlay());
        assertTrue(controller.currentUserAccessToFavorites());
        assertTrue(controller.getCurrentUserPlan().contains("Premium") || controller.getCurrentUserPlan().contains("Plano"));

        // Stats now should succeed
        assertTrue(controller.getMostReproducedMusic().startsWith("🎵"));
        assertFalse(controller.getMostReproducedArtist().startsWith("❌"));
        assertFalse(controller.getMostReproducedGenre().startsWith("❌"));

        // Rankings by users (points / playlists / reproductions)
        assertFalse(controller.getUserWithMostPoints().startsWith("❌"));
        assertFalse(controller.getUserWithMostPlaylists().startsWith("❌"));
        assertFalse(controller.getUserWithMostReproductions().startsWith("❌"));

        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);
        assertFalse(controller.getUserWithMostReproductions(start, end).startsWith("❌"));

        // Optional album extraction
        assertTrue(controller.getAlbumMusicNames("ExtraAlbum").isPresent());
        assertFalse(controller.getAlbumMusicNames("MissingAlbum").isPresent());
    }

    @Test
    void controllerStatsErrorBranches_onEmptyModel() {
        Controller controller = new Controller(new SpotifUM());
        assertTrue(controller.getMostReproducedMusic().startsWith("❌"));
        assertTrue(controller.getMostReproducedArtist().startsWith("❌"));
        assertTrue(controller.getMostReproducedGenre().startsWith("❌"));
        assertTrue(controller.getUserWithMostPoints().startsWith("❌"));
        assertTrue(controller.getUserWithMostPlaylists().startsWith("❌"));
        assertTrue(controller.getUserWithMostReproductions().startsWith("❌"));
        assertTrue(controller.getUserWithMostReproductions(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)).startsWith("❌"));
    }
}
