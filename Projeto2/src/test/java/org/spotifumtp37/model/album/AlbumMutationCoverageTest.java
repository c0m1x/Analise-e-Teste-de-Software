package org.spotifumtp37.model.album;

import org.junit.jupiter.api.Test;
import org.spotifumtp37.model.subscription.FreePlan;
import org.spotifumtp37.model.subscription.PremiumBase;
import org.spotifumtp37.model.user.History;
import org.spotifumtp37.model.user.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlbumMutationCoverageTest {

    private static Song song(String name) {
        return new Song(name, "artist", "publisher", "lyrics", "notes", "rock", 120);
    }

    private static User premium() {
        return new User("premium", "p@mail.com", "addr", new PremiumBase(), "pw", 0, new ArrayList<History>());
    }

    private static User free() {
        return new User("free", "f@mail.com", "addr", new FreePlan(), "pw", 0, new ArrayList<History>());
    }

    @Test
    void nextAndPreviousAreSequentialAndWrapAroundForPremium() throws Exception {
        Song first = song("first");
        Song second = song("second");
        Album album = new Album("album", "artist", 2024, "rock", List.of(first, second));

        album.setCurrentSong(first);
        album.next(premium());
        assertEquals("second", album.getCurrentSong().getName());
        album.next(premium());
        assertEquals("first", album.getCurrentSong().getName());

        album.previous(premium());
        assertEquals("second", album.getCurrentSong().getName());
        album.previous(premium());
        assertEquals("first", album.getCurrentSong().getName());
    }

    @Test
    void shuffleAndFreeNextKeepSingleSongAlbumStable() {
        Song only = song("only");
        Album album = new Album("album", "artist", 2024, "rock", List.of(only));
        album.setCurrentSong(only);

        album.nextShuffle();
        assertEquals("only", album.getCurrentSong().getName());
        album.next(free());
        assertEquals("only", album.getCurrentSong().getName());
    }

    @Test
    void setCurrentSongChoosesOneOfTheAlbumSongs() {
        Song first = song("first");
        Song second = song("second");
        Album album = new Album("album", "artist", 2024, "rock", List.of(first, second));

        album.setCurrentSong();

        assertTrue(List.of("first", "second").contains(album.getCurrentSong().getName()));
    }

    @Test
    void regularExplicitAndMultimediaFlagsAreObservable() {
        Song regular = song("regular");
        ExplicitSong explicit = new ExplicitSong("explicit", "artist", "publisher", "lyrics", "notes", "rock", 120);
        MultimediaSong multimedia = new MultimediaSong(explicit.getName(), explicit.getArtist(), explicit.getPublisher(),
                explicit.getLyrics(), explicit.getMusicalNotes(), explicit.getGenre(), explicit.getDurationInSeconds(), "http://video");

        assertFalse(regular.isExplicit());
        assertFalse(regular.isMultimedia());
        assertTrue(explicit.isExplicit());
        assertFalse(explicit.isMultimedia());
        assertTrue(multimedia.isMultimedia());
        assertEquals("http://video", multimedia.getVideoLink());
    }

    @Test
    void songHashCodeUsesNameArtistAndDuration() {
        Song song = new Song("name", "artist", "publisher", "lyrics", "notes", "rock", 123);

        int expected = "name".hashCode();
        expected = 31 * expected + "artist".hashCode();
        expected = 31 * expected + 123;

        assertEquals(expected, song.hashCode());
    }
}
