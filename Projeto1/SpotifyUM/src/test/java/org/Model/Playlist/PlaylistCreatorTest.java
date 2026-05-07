package org.Model.Playlist;

import org.Exceptions.AlreadyExistsException;
import org.Exceptions.EmptyPlaylistException;
import org.Model.Music.Music;
import org.Model.Music.MusicReproduction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class PlaylistCreatorTest {

    private final String NAME1 = "Bohemian Rhapsody";
    private final String INTERPRETER1 = "Queen";
    private final String PUBLISHER1 = "EMI";
    private final String LYRICS1 = "Is this the real life? Is this just fantasy?";
    private final String MUSICAL_FIGURES1 = "A-B-C-D";
    private final String GENRE1 = "Rock";
    private final String ALBUM1 = "A Night at the Opera";
    private final int DURATION1 = 355;
    private final boolean EXPLICIT1 = false;

    private final String NAME2 = "Another One Bites the Dust";
    private final String INTERPRETER2 = "Queen";
    private final String PUBLISHER2 = "EMI";
    private final String LYRICS2 = "Are you ready, hey, are you ready for this?";
    private final String MUSICAL_FIGURES2 = "E-F-G-A";
    private final String GENRE2 = "Rock";
    private final String ALBUM2 = "The Game";
    private final int DURATION2 = 215;
    private final boolean EXPLICIT2 = true;

    private final String NAME3 = "Under Pressure";
    private final String INTERPRETER3 = "Queen & David Bowie";
    private final String PUBLISHER3 = "EMI";
    private final String LYRICS3 = "Pressure pushing down on me, pressing down on you, no man ask for.";
    private final String MUSICAL_FIGURES3 = "C-D-E-F";
    private final String GENRE3 = "Rock";
    private final String ALBUM3 = "Hot Space";
    private final int DURATION3 = 240;
    private final boolean EXPLICIT3 = false;

    private Music music1;
    private Music music2;
    private Music music3;

    @BeforeEach
    void setUp() {
        music1 = new Music(NAME1, INTERPRETER1, PUBLISHER1, LYRICS1, MUSICAL_FIGURES1, GENRE1, ALBUM1, DURATION1,
                EXPLICIT1);
        music2 = new Music(NAME2, INTERPRETER2, PUBLISHER2, LYRICS2, MUSICAL_FIGURES2, GENRE2, ALBUM2, DURATION2,
                EXPLICIT2);
        music3 = new Music(NAME3, INTERPRETER3, PUBLISHER3, LYRICS3, MUSICAL_FIGURES3, GENRE3, ALBUM3, DURATION3,
                EXPLICIT3);
    }

    @Test
    void testCreateGenrePlaylistSuccess() throws AlreadyExistsException, EmptyPlaylistException {
        Map<String, Music> musics = new HashMap<>();
        musics.put("Song1", music1);
        musics.put("Song2", music2);
        musics.put("Song3", music3);

        List<Music> playlist = PlaylistCreator.createGenrePlaylist("user1", "RockPlaylist", "Rock", 400, musics,
                new HashMap<>());

        assertNotNull(playlist);
        assertFalse(playlist.isEmpty());
        assertTrue(playlist.stream().allMatch(m -> m.getGenre().equals("Rock")));
        assertTrue(playlist.stream().mapToInt(Music::getDuration).sum() <= 400);
    }

    @Test
    void testCreateGenrePlaylistNoGenre() {
        Map<String, Music> musics = new HashMap<>();
        musics.put("Song1", music1);

        assertThrows(IllegalArgumentException.class,
                () -> PlaylistCreator.createGenrePlaylist("user1", "RockPlaylist", "Pop", 300, musics, new HashMap<>()));
    }

    @Test
    void testCreateGenrePlaylistEmptyPlaylist() {
        Map<String, Music> musics = new HashMap<>();
        musics.put("Song1", music1);

        assertThrows(EmptyPlaylistException.class,
                () -> PlaylistCreator.createGenrePlaylist("user1", "RockPlaylist", "Rock", 300, musics, new HashMap<>()));
    }

    @Test
    void createGenrePlaylistIncludesSongThatExactlyFitsDuration() throws Exception {
        Map<String, Music> musics = new HashMap<>();
        musics.put("exact", music2);
        musics.put("tooLong", music1);

        List<Music> playlist = PlaylistCreator.createGenrePlaylist(
                "user1", "ExactFit", "Rock", music2.getDuration(), musics, new HashMap<>());

        assertEquals(1, playlist.size());
        assertEquals(music2.getName(), playlist.get(0).getName());
    }

    @Test
    void testCreateRandomPlaylist() {
        Map<String, Music> musics = new HashMap<>();
        musics.put("Song1", music1);
        musics.put("Song2", music2);
        musics.put("Song3", music3);

        List<Music> playlist = PlaylistCreator.createRandomPlaylist(musics);

        assertNotNull(playlist);
        assertFalse(playlist.isEmpty());
        assertTrue(playlist.size() <= musics.size());
    }

    @Test
    void createRandomPlaylistWithSingleSongReturnsExactlyThatSong() {
        Map<String, Music> musics = new HashMap<>();
        musics.put(music1.getName(), music1);

        List<Music> playlist = assertDoesNotThrow(() -> PlaylistCreator.createRandomPlaylist(musics));

        assertEquals(1, playlist.size());
        assertEquals(music1.getName(), playlist.get(0).getName());
    }

    @Test
    void testCreateFavoritesPlaylistSuccess() {
        Map<String, Music> musics = new HashMap<>();
        musics.put(music1.getName(), music1);
        musics.put(music2.getName(), music2);
        musics.put(music3.getName(), music3);

        List<MusicReproduction> reproductions = Arrays.asList(
                new MusicReproduction(music1),
                new MusicReproduction(music2),
                new MusicReproduction(music3));

        List<Music> playlist = PlaylistCreator.createFavoritesPlaylist(500, false, reproductions, musics);

        assertNotNull(playlist);
        assertFalse(playlist.isEmpty());
        assertTrue(playlist.stream().mapToInt(Music::getDuration).sum() <= 500);
    }

    @Test
    void testCreateFavoritesPlaylistExplicitOnly() {
        Map<String, Music> musics = new HashMap<>();
        musics.put(music1.getName(), music1);
        musics.put(music2.getName(), music2);

        // Mark one music as explicit so explicit filter yields posts
        music2.setExplicit(true);
        musics.put(music2.getName(), music2);

        List<MusicReproduction> reproductions = Arrays.asList(
                new MusicReproduction(music1),
                new MusicReproduction(music2),
                new MusicReproduction(music2));

        List<Music> playlist = PlaylistCreator.createFavoritesPlaylist(500, true, reproductions, musics);

        assertNotNull(playlist);
        assertFalse(playlist.isEmpty());
        assertTrue(playlist.stream().allMatch(Music::isExplicit));
    }

    @Test
    void testCreateFavoritesPlaylistNoFavorites() {
        Map<String, Music> musics = new HashMap<>();
        musics.put(music1.getName(), music1);

        List<MusicReproduction> reproductions = Collections.emptyList();

        assertThrows(IllegalArgumentException.class,
                () -> PlaylistCreator.createFavoritesPlaylist(500, false, reproductions, musics));
    }

    @Test
    void favoritesPlaylistUsesPlayCountOrderingExplicitFilterAndExactDuration() {
        Music favorite = new Music("favorite", INTERPRETER1, PUBLISHER1, LYRICS1, MUSICAL_FIGURES1,
                GENRE1, ALBUM1, 100, true);
        Music runnerUp = new Music("runner-up", INTERPRETER1, PUBLISHER1, LYRICS1, MUSICAL_FIGURES1,
                GENRE1, ALBUM1, 100, false);
        Music rare = new Music("rare", INTERPRETER1, PUBLISHER1, LYRICS1, MUSICAL_FIGURES1,
                GENRE1, ALBUM1, 100, false);
        Map<String, Music> musics = new HashMap<>();
        musics.put(favorite.getName(), favorite);
        musics.put(runnerUp.getName(), runnerUp);
        musics.put(rare.getName(), rare);
        List<MusicReproduction> reproductions = Arrays.asList(
                new MusicReproduction(rare),
                new MusicReproduction(runnerUp),
                new MusicReproduction(runnerUp),
                new MusicReproduction(favorite),
                new MusicReproduction(favorite),
                new MusicReproduction(favorite)
        );

        List<Music> ordered = PlaylistCreator.createFavoritesPlaylist(300, false, reproductions, musics);
        assertEquals(Arrays.asList("favorite", "runner-up", "rare"),
                ordered.stream().map(Music::getName).toList());

        List<Music> exactFit = PlaylistCreator.createFavoritesPlaylist(200, false, reproductions, musics);
        assertEquals(Arrays.asList("favorite", "runner-up"),
                exactFit.stream().map(Music::getName).toList());

        List<Music> explicitOnly = PlaylistCreator.createFavoritesPlaylist(300, true, reproductions, musics);
        assertEquals(List.of("favorite"), explicitOnly.stream().map(Music::getName).toList());

        List<Music> unlimited = PlaylistCreator.createFavoritesPlaylist(0, false, reproductions, musics);
        assertEquals(Arrays.asList("favorite", "runner-up", "rare"),
                unlimited.stream().map(Music::getName).toList());
    }
}
