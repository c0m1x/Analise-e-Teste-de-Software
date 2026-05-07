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
        IndexOutOfBoundsException lower = assertThrows(IndexOutOfBoundsException.class, () -> playlist.getMusicBYIndex(-1));
        IndexOutOfBoundsException upper = assertThrows(IndexOutOfBoundsException.class, () -> playlist.getMusicBYIndex(2));
        assertTrue(lower.getMessage().contains("limites"));
        assertTrue(upper.getMessage().contains("limites"));
        assertThrows(AlreadyExistsException.class, () -> playlist.addMusic(first));

        assertTrue(playlist.removeMusic(first));
        assertFalse(playlist.removeMusic(first));
    }

    @Test
    void everyConstructorAdvancesTheStaticIdCounter() {
        Playlist withSongs1 = new Playlist("with-1", "u", List.of(song("a")));
        Playlist withSongs2 = new Playlist("with-2", "u", List.of(song("b")));
        assertEquals(withSongs1.getId() + 1, withSongs2.getId());

        Playlist empty1 = new Playlist("empty-1", "u");
        Playlist empty2 = new Playlist("empty-2", "u");
        assertEquals(empty1.getId() + 1, empty2.getId());

        Playlist default1 = new Playlist();
        Playlist default2 = new Playlist();
        assertEquals(default1.getId() + 1, default2.getId());

        Playlist base = new Playlist("base", "u");
        new Playlist(base);
        Playlist afterCopy = new Playlist("after-copy", "u");
        assertEquals(base.getId() + 2, afterCopy.getId());
    }
}
