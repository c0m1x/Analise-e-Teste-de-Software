package org.Model;

import java.util.HashMap;
import java.util.Map;

import org.Exceptions.NoArtistsInDatabaseException;
import org.Model.Album.Album;
import org.Model.Music.Music;
import org.Model.User.User;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class SpotifUMPitSurvivorTest {

    @Test
    void gettersReturnNonEmptyCopiesWhenStateIsNonEmpty() {
        SpotifUM spotifUM = new SpotifUM();

        Map<String, Music> musics = new HashMap<>();
        musics.put(
                "m1",
                new Music("m1", "artist", "pub", "la la", "fig", "rock", "alb", 120, false)
        );
        spotifUM.setMusics(musics);

        Map<String, Album> albums = new HashMap<>();
        albums.put("alb", new Album("alb", "artist"));
        spotifUM.setAlbums(albums);

        Map<String, User> users = new HashMap<>();
        users.put("u1", new User("u1", "u1@example.com", "addr", "pw"));
        spotifUM.setUsers(users);

        assertEquals(1, spotifUM.getMusics().size());
        assertTrue(spotifUM.getMusics().containsKey("m1"));

        assertEquals(1, spotifUM.getAlbums().size());
        assertTrue(spotifUM.getAlbums().containsKey("alb"));

        assertEquals(1, spotifUM.getUsers().size());
        assertTrue(spotifUM.getUsers().containsKey("u1"));
    }

    @Test
    void incrementReproductionsActuallyIncrements() {
        SpotifUM spotifUM = new SpotifUM();

        spotifUM.incrementArtistReproductions("A");
        spotifUM.incrementArtistReproductions("A");
        assertEquals(2, spotifUM.getArtistReproductions().get("A"));

        spotifUM.incrementGenreReproductions("Rock");
        spotifUM.incrementGenreReproductions("Rock");
        assertEquals(2, spotifUM.getGenreReproductions().get("Rock"));
    }

    @Test
    void topArtistNameIsSortedByCountDesc() throws NoArtistsInDatabaseException {
        SpotifUM spotifUM = new SpotifUM();

        Map<String, Integer> artistRepro = new HashMap<>();
        artistRepro.put("A", 1);
        artistRepro.put("B", 2);
        spotifUM.setArtistReproductions(artistRepro);

        String report = spotifUM.getTopArtistName();
        assertFalse(report.isEmpty());
        assertTrue(report.startsWith("B - 2"), "Expected highest-count artist first, got: " + report);
        assertTrue(report.contains("A - 1"));
    }

    @Test
    void equalsIsNotTriviallyTrue() {
        SpotifUM s1 = new SpotifUM();
        SpotifUM s2 = new SpotifUM();

        Map<String, Music> musics = new HashMap<>();
        musics.put(
                "m1",
                new Music("m1", "artist", "pub", "la la", "fig", "rock", "alb", 120, false)
        );
        s1.setMusics(musics);

        assertEquals(s1, s1);
        assertNotEquals(s1, s2);
        assertNotEquals(s1, "not a SpotifUM");
    }
}
