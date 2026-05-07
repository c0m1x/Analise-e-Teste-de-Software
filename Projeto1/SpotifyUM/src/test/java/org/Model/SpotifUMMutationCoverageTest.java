package org.Model;

import org.Exceptions.AlreadyExistsException;
import org.Exceptions.EmptyPlaylistException;
import org.Exceptions.NotFoundException;
import org.Model.Album.Album;
import org.Model.Music.Music;
import org.Model.Music.MusicMultimedia;
import org.Model.Plan.PlanPremiumBase;
import org.Model.Plan.PlanPremiumTop;
import org.Model.Playlist.Playlist;
import org.Model.Playlist.PlaylistCreator;
import org.Model.User.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SpotifUMMutationCoverageTest {

    private static Music song(String name, String artist, String genre, int duration, boolean explicit) {
        return new Music(name, artist, "publisher", "lyrics", "notes", genre, "album", duration, explicit);
    }

    @Test
    void parameterizedAndCopyConstructorsCopyEveryMapAndCurrentUser() {
        Music music = song("song", "artist", "rock", 120, false);
        Playlist playlist = new Playlist("playlist", "owner", List.of(music));
        User user = new User("owner", "owner@mail.com", "addr", "pw");
        user.setPlan(new PlanPremiumTop());
        Album album = new Album("album", "artist");

        Map<String, Music> musics = new HashMap<>();
        musics.put(music.getName(), music);
        Map<Integer, Playlist> playlists = new HashMap<>();
        playlists.put(playlist.getId(), playlist);
        Map<String, User> users = new HashMap<>();
        users.put(user.getUsername(), user);
        Map<String, Album> albums = new HashMap<>();
        albums.put(album.getName(), album);
        Map<String, Integer> artistStats = new HashMap<>();
        artistStats.put("artist", 3);
        Map<String, Integer> genreStats = new HashMap<>();
        genreStats.put("rock", 4);

        SpotifUM model = new SpotifUM(musics, playlists, users, albums, artistStats, genreStats);
        model.setCurrentUser(user);

        assertEquals(1, model.getMusics().size());
        assertEquals(1, model.getPublicPlaylists().size());
        assertEquals(1, model.getUsers().size());
        assertEquals(1, model.getAlbums().size());
        assertEquals(3, model.getArtistReproductions().get("artist"));
        assertEquals(4, model.getGenreReproductions().get("rock"));

        SpotifUM copy = new SpotifUM(model);
        assertNotSame(model.getCurrentUser(), copy.getCurrentUser());
        assertEquals("owner", copy.getCurrentUser().getUsername());
        assertEquals(model.getMusics().keySet(), copy.getMusics().keySet());
        assertEquals(model.getPublicPlaylists().keySet(), copy.getPublicPlaylists().keySet());
        assertEquals(model.getUsers().keySet(), copy.getUsers().keySet());
        assertEquals(model.getAlbums().keySet(), copy.getAlbums().keySet());
        assertEquals(model.getArtistReproductions(), copy.getArtistReproductions());
        assertEquals(model.getGenreReproductions(), copy.getGenreReproductions());
    }

    @Test
    void profileSettersAndLibraryPermissionsChangeObservableState() throws Exception {
        SpotifUM model = new SpotifUM();
        model.addNewUser("u", "u@mail.com", "addr", "pw");
        model.authenticateUser("u", "pw");

        assertFalse(model.hasLibrary());
        assertFalse(model.canCurrentUserSkip());
        assertFalse(model.canCurrentUserChooseWhatToPlay());
        assertFalse(model.currentUserAccessToFavorites());

        model.getCurrentUser().setPlan(new PlanPremiumBase());
        assertTrue(model.hasLibrary());
        assertTrue(model.canCurrentUserSkip());
        assertTrue(model.canCurrentUserChooseWhatToPlay());
        assertFalse(model.currentUserAccessToFavorites());

        model.setCurrentUserEmail("new@mail.com");
        model.setCurrentUserUsername("renamed-directly");
        assertEquals("new@mail.com", model.getCurrentUser().getEmail());
        assertEquals("renamed-directly", model.getCurrentUser().getUsername());
    }

    @Test
    void addingMusicAlsoAddsItToTheTargetAlbum() throws Exception {
        SpotifUM model = new SpotifUM();
        model.addNewAlbum("album", "artist");

        model.addNewMusic("plain", "artist", "publisher", "lyrics", "notes", "rock", "album", 120, false, null);
        model.addNewMusic("video", "artist", "publisher", "lyrics", "notes", "rock", "album", 130, true, "http://video");

        Album album = model.getAlbumByName("album");
        assertEquals(2, album.getMusics().size());
        assertTrue(album.getMusics().stream().anyMatch(m -> m.getName().equals("plain")));
        assertTrue(album.getMusics().stream().anyMatch(m -> m.getName().equals("video")));
        assertFalse(model.getMusicByName("plain") instanceof MusicMultimedia);
        assertInstanceOf(MusicMultimedia.class, model.getMusicByName("video"));
        assertThrows(AlreadyExistsException.class,
                () -> model.addNewMusic("plain", "artist", "publisher", "lyrics", "notes", "rock", "album", 120, false, null));
    }

    @Test
    void addMusicToAlbumRequiresExistingAlbumAndMutatesAlbum() throws Exception {
        SpotifUM model = new SpotifUM();
        Music music = song("song", "artist", "rock", 120, false);
        model.addNewAlbum("album", "artist");

        model.addMusicToAlbum("album", music);

        assertEquals(1, model.getAlbumByName("album").getMusics().size());
        assertEquals("song", model.getAlbumByName("album").getMusics().get(0).getName());
        assertThrows(NotFoundException.class, () -> model.addMusicToAlbum("missing", music));
    }

    @Test
    void publicPlaylistsSetterAndGetterPreserveNonEmptyContent() {
        SpotifUM model = new SpotifUM();
        Playlist playlist = new Playlist("public", "owner");
        model.setPublicPlaylists(Map.of(playlist.getId(), playlist));

        assertEquals(1, model.getPublicPlaylistSize());
        assertEquals("public", model.getPublicPlaylistById(playlist.getId()).getName());
        assertEquals(1, model.getPublicPlaylists().size());
    }

    @Test
    void playlistCreatorHonorsGenreDurationExplicitFilterAndOrdering() throws Exception {
        Music slow = song("slow", "a", "rock", 200, false);
        Music fast = song("fast", "a", "rock", 50, false);
        Music pop = song("pop", "a", "pop", 50, false);
        Map<String, Music> musics = Map.of("slow", slow, "fast", fast, "pop", pop);

        List<Music> rock = PlaylistCreator.createGenrePlaylist("u", "rock", "rock", 60, musics, Map.of());
        assertEquals(1, rock.size());
        assertEquals("fast", rock.get(0).getName());

        assertThrows(IllegalArgumentException.class,
                () -> PlaylistCreator.createGenrePlaylist("u", "none", "jazz", 60, musics, Map.of()));
        assertThrows(EmptyPlaylistException.class,
                () -> PlaylistCreator.createGenrePlaylist("u", "tiny", "rock", 1, musics, Map.of()));
    }

    @Test
    void directPlaybackUsernameChangesAndGeneratedPlaylistsMutateVisibleState() throws Exception {
        SpotifUM model = new SpotifUM();
        model.addNewUser("u", "u@mail.com", "addr", "pw");
        model.authenticateUser("u", "pw");
        model.getCurrentUser().setPlan(new PlanPremiumTop());
        model.addNewAlbum("album", "artist");
        model.addNewMusic("plain", "artist", "publisher", "plain lyrics", "notes", "rock", "album", 120, false, null);

        assertEquals("plain lyrics", model.playMusic("plain"));

        model.addToCurrentUserPlaylist("owned");
        model.changeCurrentUserName("renamed");
        assertTrue(model.userExists("renamed"));
        assertFalse(model.userExists("u"));
        assertEquals("renamed", model.getCurrentUser().getPlaylists().get(0).getAutor());

        int before = model.getCurrentUser().getPlaylists().size();
        model.createGenrePlaylist("rock picks", "rock", 120);
        assertEquals(before + 1, model.getCurrentUser().getPlaylists().size());
        assertTrue(model.getCurrentUser().getPlaylists().stream()
                .anyMatch(p -> p.getName().equals("rock picks") && p.getAutor().equals("renamed")));
    }

    @Test
    void statisticsChooseTheRealMaximumInsteadOfFirstEncounteredValue() throws Exception {
        Music low = song("low", "artist", "rock", 120, false);
        Music high = song("high", "artist", "pop", 120, false);
        low.setReproductions(1);
        high.setReproductions(3);

        SpotifUM model = new SpotifUM();
        model.setMusics(Map.of(low.getName(), low, high.getName(), high));
        model.setGenreReproductions(Map.of("rock", 1, "pop", 3));

        assertEquals("high", model.mostReproducedMusic().getName());
        assertEquals("pop", model.getGenreWithMostReproductions());

        User fewPlaylists = new User("a", "few@mail.com", "addr", "pw");
        fewPlaylists.setPlaylists(new ArrayList<>(List.of(new Playlist("one", "few"))));
        User manyPlaylists = new User("z", "many@mail.com", "addr", "pw");
        manyPlaylists.setPlaylists(new ArrayList<>(List.of(
                new Playlist("one", "z"),
                new Playlist("two", "z"),
                new Playlist("foreign", "someone"))));
        model.setUsers(Map.of("a", fewPlaylists, "z", manyPlaylists));

        assertEquals("z", model.getUserWithMostPlaylists().getUsername());

        fewPlaylists.addMusicReproduction(low);
        manyPlaylists.addMusicReproduction(high);
        manyPlaylists.addMusicReproduction(low);
        model.setUsers(Map.of("a", fewPlaylists, "z", manyPlaylists));

        assertEquals("z", model.getUserWithMostReproductions().getUsername());
        assertEquals("z", model.getUserWithMostReproductions(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)).getUsername());
    }

    @Test
    void missingPlaylistListingAndNullCurrentUserEqualityBranchesAreObservable() throws NotFoundException {
        SpotifUM model = new SpotifUM();
        model.addNewUser("u", "u@mail.com", "addr", "pw");
        assertDoesNotThrow(() -> model.authenticateUser("u", "pw"));
        model.getCurrentUser().setPlan(new PlanPremiumBase());

        assertTrue(model.listAllMusicsInPlaylist(999).contains("999"));

        SpotifUM withUser = new SpotifUM();
        withUser.setCurrentUser(new User("u", "u@mail.com", "addr", "pw"));
        assertNotEquals(new SpotifUM(), withUser);
    }

    @Test
    void populateDatabaseAddsSeedUsersAndPublicPlaylists() {
        SpotifUM model = new SpotifUM();

        model.populateDatabase();

        assertTrue(model.getUsers().keySet().containsAll(List.of("simao", "gabriel", "jose")));
        assertTrue(model.getPublicPlaylists().size() >= 6);
    }
}
