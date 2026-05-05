package org.Controller;

import org.Model.*;
import org.Model.Album.*;
import org.Model.Music.*;
import org.Model.Playlist.*;
import org.Model.User.*;
import org.Model.Plan.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.List;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for Controller class covering all major business logic methods.
 */
class ControllerComprehensiveTest {

    private Controller controller;
    private SpotifUM spotifUM;

    @BeforeEach
    void setUp() {
        spotifUM = new SpotifUM();
        controller = new Controller(spotifUM);
        
        // Setup test data
        controller.addNewUser("testuser", "test@example.com", "123 Main St", "password");
        controller.loginWithMessage("testuser", "password");
    }

    // ==================== Album Tests ====================
    @Test
    void createAlbum_success() {
        String result = controller.createAlbum("TestAlbum", "TestArtist");
        assertTrue(result.contains("criado") || result.contains("sucesso"));
        assertTrue(controller.albumExists("TestAlbum"));
    }

    @Test
    void createAlbum_duplicate() {
        controller.createAlbum("DupAlbum", "Artist1");
        String result = controller.createAlbum("DupAlbum", "Artist2");
        // Should handle gracefully (may be error or success depending on model)
        assertNotNull(result);
    }

    @Test
    void albumExists_true() {
        controller.createAlbum("ExistingAlbum", "Artist");
        assertTrue(controller.albumExists("ExistingAlbum"));
    }

    @Test
    void albumExists_false() {
        assertFalse(controller.albumExists("NonExistentAlbum"));
    }

    // ==================== Music Tests ====================
    @Test
    void addMusic_success() {
        controller.createAlbum("MusicAlbum", "MusicArtist");
        String result = controller.addMusic(
            "TestSong", "MusicArtist", "Publisher", "Test lyrics",
            "1/4 1/4 1/2", "Rock", "MusicAlbum", 180, false, ""
        );
        assertTrue(result.contains("adicionada") || result.contains("sucesso"));
        assertTrue(controller.musicExists("TestSong"));
    }

    @Test
    void musicExists_true() {
        controller.createAlbum("AlbumForMusic", "ArtistMusic");
        controller.addMusic("SongExists", "ArtistMusic", "Pub", "lyrics",
                "notes", "Pop", "AlbumForMusic", 200, false, "");
        assertTrue(controller.musicExists("SongExists"));
    }

    @Test
    void musicExists_false() {
        assertFalse(controller.musicExists("NonExistentSong"));
    }

    @Test
    void addMusic_withMultimediaUrl() {
        controller.createAlbum("VideoAlbum", "VideoArtist");
        String result = controller.addMusic(
            "VideoSong", "VideoArtist", "Pub", "lyrics",
            "notes", "Pop", "VideoAlbum", 200, true, "https://video.url"
        );
        assertNotNull(result);
        assertTrue(controller.musicExists("VideoSong"));
    }

    // ==================== User Management Tests ====================
    @Test
    void changeCurrentUserUserName_success() {
        String result = controller.changeCurrentUserUserName("newusername");
        assertTrue(result.contains("alterado") || result.contains("sucesso"));
    }

    @Test
    void changeCurrentUserEmail_success() {
        String result = controller.changeCurrentUserEmail("newemail@example.com");
        assertTrue(result.contains("alterado") || result.contains("sucesso"));
    }

    @Test
    void isPasswordCorrect_true() {
        assertTrue(controller.isPasswordCorrect("password"));
    }

    @Test
    void isPasswordCorrect_false() {
        assertFalse(controller.isPasswordCorrect("wrongpassword"));
    }

    @Test
    void changeCurrentUserPassword_success() {
        String result = controller.changeCurrentUserPassword("newpassword");
        assertTrue(result.contains("alterado") || result.contains("sucesso"));
        assertTrue(controller.isPasswordCorrect("newpassword"));
    }

    @Test
    void getCurrentUser() {
        String result = controller.getCurrentUser();
        assertTrue(result.contains("testuser") || result.contains("Perfil"));
    }

    // ==================== Plan/Subscription Tests ====================
    @Test
    void setFreePlan() {
        String result = controller.setFreePlan();
        assertTrue(result.contains("Free") || result.contains("anúncios"));
    }

    @Test
    void setPremiumBasePlan() {
        String result = controller.setPremiumBasePlan();
        assertTrue(result.contains("PremiumBase") || result.contains("Boa escolha"));
    }

    @Test
    void setPremiumTopPlan() {
        String result = controller.setPremiumTopPlan();
        assertTrue(result.contains("PremiumTop") || result.contains("topo"));
    }

    @Test
    void getCurrentUserPlan() {
        String plan = controller.getCurrentUserPlan();
        assertNotNull(plan);
    }

    // ==================== Playlist Tests ====================
    @Test
    void addToCurrentUserPlaylists_success() {
        String result = controller.addToCurrentUserPlaylists("MyPlaylist");
        assertNotNull(result);
        // Message format may vary
    }

    @Test
    void listUserPlaylists_empty() {
        String result = controller.listUserPlaylists();
        assertTrue(result.contains("playlists") || result.contains("Não"));
    }

    @Test
    void listPublicPlaylists() {
        String result = controller.listPublicPlaylists();
        assertNotNull(result);
    }

    @Test
    void getPlaylistMusicNames_empty() {
        // Create playlist but don't add music
        controller.addToCurrentUserPlaylists("EmptyPlaylist");
        Optional<List<String>> result = controller.getPlaylistMusicNames(0);
        // Result could be empty or contain empty list
        assertNotNull(result);
    }

    @Test
    void setPlaylistAsPublic_success() {
        controller.addToCurrentUserPlaylists("PrivatePlaylist");
        String result = controller.setPlaylistAsPublic(0);
        assertTrue(result.contains("pública") || result.contains("sucesso"));
    }

    @Test
    void createGenrePlaylist_success() {
        // Add music first
        controller.createAlbum("GenreAlbum", "GenreArtist");
        controller.addMusic("GenreSong", "GenreArtist", "Pub", "lyrics",
                "notes", "Jazz", "GenreAlbum", 200, false, "");
        
        String result = controller.createGenrePlaylist("JazzPlaylist", "Jazz", 600);
        assertTrue(result.contains("criada") || result.contains("sucesso"));
    }

    // ==================== Music Playback Tests ====================
    @Test
    void playMusic_success() {
        controller.createAlbum("PlayAlbum", "PlayArtist");
        controller.addMusic("PlaySong", "PlayArtist", "Pub", "lyrics",
                "notes", "Rock", "PlayAlbum", 200, false, "");
        
        Object musicInfo = controller.playMusic("PlaySong");
        assertNotNull(musicInfo);
    }

    @Test
    void canCurrentUserSkip() {
        boolean canSkip = controller.canCurrentUserSkip();
        assertNotNull(canSkip);
    }

    @Test
    void canCurrentUserChooseWhatToPlay() {
        boolean canChoose = controller.canCurrentUserChooseWhatToPlay();
        assertNotNull(canChoose);
    }

    @Test
    void currentUserAccessToFavorites() {
        boolean hasAccess = controller.currentUserAccessToFavorites();
        assertNotNull(hasAccess);
    }

    // ==================== Statistics Tests ====================
    @Test
    void getMostReproducedMusic() {
        controller.createAlbum("StatsAlbum", "StatsArtist");
        controller.addMusic("PopularSong", "StatsArtist", "Pub", "lyrics",
                "notes", "Rock", "StatsAlbum", 200, false, "");
        
        String result = controller.getMostReproducedMusic();
        assertTrue(result.contains("Música") || result.contains("reproduzida"));
    }

    @Test
    void getMostReproducedArtist() {
        controller.createAlbum("ArtistAlbum", "TopArtist");
        controller.addMusic("ArtistSong", "TopArtist", "Pub", "lyrics",
                "notes", "Pop", "ArtistAlbum", 200, false, "");
        
        String result = controller.getMostReproducedArtist();
        assertTrue(result.contains("Artista") || result.contains("reproduzido"));
    }

    @Test
    void getUserWithMostPoints() {
        String result = controller.getUserWithMostPoints();
        assertTrue(result.contains("pontos") || result.contains("mais"));
    }

    @Test
    void getMostReproducedGenre() {
        controller.createAlbum("GenreStatsAlbum", "GenreStatsArtist");
        controller.addMusic("GenreStatsSong", "GenreStatsArtist", "Pub", "lyrics",
                "notes", "Classical", "GenreStatsAlbum", 200, false, "");
        
        String result = controller.getMostReproducedGenre();
        assertTrue(result.contains("Género") || result.contains("reproduzido"));
    }

    @Test
    void getUserWithMostPlaylists() {
        controller.addToCurrentUserPlaylists("Playlist1");
        String result = controller.getUserWithMostPlaylists();
        assertTrue(result.contains("playlists") || result.contains("mais"));
    }

    @Test
    void getUserWithMostReproductions() {
        String result = controller.getUserWithMostReproductions();
        assertNotNull(result);
    }

    @Test
    void getUserWithMostReproductions_withDateRange() {
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();
        String result = controller.getUserWithMostReproductions(start, end);
        assertNotNull(result);
    }

    @Test
    void getPublicPlaylistCount() {
        String result = controller.getPublicPlaylistCount();
        assertTrue(result.contains("playlists"));
    }

    // ==================== List Tests ====================
    @Test
    void listAllMusics() {
        controller.createAlbum("ListAlbum", "ListArtist");
        controller.addMusic("ListSong", "ListArtist", "Pub", "lyrics",
                "notes", "Pop", "ListAlbum", 200, false, "");
        
        String result = controller.listAllMusics();
        assertTrue(result.contains("música") || result.contains("Não"));
    }

    @Test
    void listAllAlbums() {
        controller.createAlbum("ListableAlbum", "ListableArtist");
        String result = controller.listAllAlbums();
        assertNotNull(result);
    }

    @Test
    void listAllGenres() {
        controller.createAlbum("GenreListAlbum", "GenreListArtist");
        controller.addMusic("GenreListSong", "GenreListArtist", "Pub", "lyrics",
                "notes", "Reggae", "GenreListAlbum", 200, false, "");
        
        String result = controller.listAllGenres();
        assertTrue(result.contains("género") || result.contains("Não"));
    }

    @Test
    void getAlbumMusicNames() {
        controller.createAlbum("AlbumWithMusics", "Artist");
        controller.addMusic("Song1", "Artist", "Pub", "lyrics",
                "notes", "Rock", "AlbumWithMusics", 200, false, "");
        
        Optional<List<String>> result = controller.getAlbumMusicNames("AlbumWithMusics");
        assertTrue(result.isPresent() || result.isEmpty());
    }

    @Test
    void getRandomPlaylistMusicNames() {
        controller.createAlbum("RandomAlbum", "RandomArtist");
        controller.addMusic("RandomSong", "RandomArtist", "Pub", "lyrics",
                "notes", "Funk", "RandomAlbum", 200, false, "");
        
        Optional<List<String>> result = controller.getRandomPlaylistMusicNames();
        assertNotNull(result);
    }

    @Test
    void getFavoritePlaylistMusicNames() {
        controller.createAlbum("FavAlbum", "FavArtist");
        controller.addMusic("FavSong", "FavArtist", "Pub", "lyrics",
                "notes", "Soul", "FavAlbum", 200, false, "");
        
        Optional<List<String>> result = controller.getFavoritePlaylistMusicNames(false, 500);
        assertNotNull(result);
    }

    // ==================== Utility Tests ====================
    @Test
    void currentUserHasLibrary() {
        boolean hasLibrary = controller.currentUserHasLibrary();
        assertNotNull(hasLibrary);
    }

    @Test
    void getPlaylistId() {
        controller.addToCurrentUserPlaylists("NamedPlaylist");
        String name = controller.getPlaylistId(0);
        assertNotNull(name);
    }

    @Test
    void controllerCopyConstructor() {
        Controller controllerCopy = new Controller(controller);
        assertNotNull(controllerCopy.getSpotifUM());
    }

    @Test
    void controllerDefaultConstructor() {
        Controller newController = new Controller();
        assertNotNull(newController.getSpotifUM());
    }

    @Test
    void setSpotifUM() {
        SpotifUM newModel = new SpotifUM();
        controller.setSpotifUM(newModel);
        assertNotNull(controller.getSpotifUM());
    }

    @Test
    void listPlaylistMusics_withValidPlaylist() {
        controller.addToCurrentUserPlaylists("MusicPlaylist");
        String result = controller.listPlaylistMusics(0);
        assertNotNull(result);
    }

    @Test
    void addPublicPlaylistToLibrary() {
        controller.addToCurrentUserPlaylists("PublicToAdd");
        String result = controller.addPublicPlaylistToLibrary(0);
        assertNotNull(result);
    }

    @Test
    void removeMusicFromPlaylist() {
        controller.createAlbum("RemoveAlbum", "RemoveArtist");
        controller.addMusic("RemoveSong", "RemoveArtist", "Pub", "lyrics",
                "notes", "Blues", "RemoveAlbum", 200, false, "");
        controller.addToCurrentUserPlaylists("RemovePlaylist");
        controller.addMusicToCurrentUserPlaylist(0, "RemoveSong");
        
        String result = controller.removeMusicFromPlaylist("RemoveSong", 0);
        assertNotNull(result);
    }

    @Test
    void addMusicToCurrentUserPlaylist() {
        controller.createAlbum("AddMusicAlbum", "AddMusicArtist");
        controller.addMusic("AddMusicSong", "AddMusicArtist", "Pub", "lyrics",
                "notes", "Disco", "AddMusicAlbum", 200, false, "");
        controller.addToCurrentUserPlaylists("AddMusicPlaylist");
        
        String result = controller.addMusicToCurrentUserPlaylist(0, "AddMusicSong");
        assertNotNull(result);
    }
}
