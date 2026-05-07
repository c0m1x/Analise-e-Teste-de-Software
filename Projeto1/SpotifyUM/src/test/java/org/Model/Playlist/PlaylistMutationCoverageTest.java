package org.Model.Playlist;

import org.Exceptions.AlreadyExistsException;
import org.Model.Music.Music;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistMutationCoverageTest {

    private static Music song(String name) {
        return new Music(name, "artist", "publisher", "lyrics", "notes", "rock", "album", 120, false);
    }

    @Test
    void equalsRejectsDifferentNameAuthorSongsTypeAndNull() {
        Music first = song("first");
        Music second = song("second");
        Playlist base = new Playlist("p", "u", List.of(first));

        assertEquals(base, base);
        assertEquals(base, new Playlist("p", "u", List.of(first)));
        assertNotEquals(base, null);
        assertNotEquals(base, "playlist");
        assertNotEquals(base, new Playlist("other", "u", List.of(first)));
        assertNotEquals(base, new Playlist("p", "other", List.of(first)));
        assertNotEquals(base, new Playlist("p", "u", List.of(second)));
    }

    @Test
    void addGetAndRemoveMusicExerciseBoundaries() throws Exception {
        Music first = song("first");
        Music second = song("second");
        Playlist playlist = new Playlist("p", "u");

        playlist.addMusic(first);
        playlist.addMusic(second);

        assertEquals("first", playlist.getMusicBYIndex(0).getName());
        assertEquals("second", playlist.getMusicBYIndex(1).getName());
        assertThrows(IndexOutOfBoundsException.class, () -> playlist.getMusicBYIndex(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> playlist.getMusicBYIndex(2));
        assertThrows(AlreadyExistsException.class, () -> playlist.addMusic(first));

        assertTrue(playlist.removeMusic(first));
        assertFalse(playlist.removeMusic(first));
    }
}
