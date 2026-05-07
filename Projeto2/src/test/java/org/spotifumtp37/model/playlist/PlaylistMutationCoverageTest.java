package org.spotifumtp37.model.playlist;

import org.junit.jupiter.api.Test;
import org.spotifumtp37.exceptions.SubscriptionDoesNotAllowException;
import org.spotifumtp37.model.album.Song;
import org.spotifumtp37.model.subscription.FreePlan;
import org.spotifumtp37.model.subscription.PremiumBase;
import org.spotifumtp37.model.user.History;
import org.spotifumtp37.model.user.User;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistMutationCoverageTest {

    private static Song song(String name) {
        return new Song(name, "artist", "publisher", "lyrics", "notes", "rock", 120);
    }

    private static User premium(String name) {
        return new User(name, name + "@mail.com", "addr", new PremiumBase(), "pw", 0, new ArrayList<History>());
    }

    private static User free(String name) {
        return new User(name, name + "@mail.com", "addr", new FreePlan(), "pw", 0, new ArrayList<History>());
    }

    @Test
    void playMutatesSongUserPointsAndHistoryAndIgnoresNulls() {
        Song first = song("first");
        Playlist playlist = new Playlist(premium("creator"), "p", "desc", 0, "public", List.of(first));
        playlist.setCurrentSong(first);
        User user = premium("listener");

        playlist.play(null);
        assertEquals(0, first.getTimesPlayed());

        playlist.play(user);
        assertEquals(1, first.getTimesPlayed());
        assertTrue(user.getPontos() > 0);
        assertEquals(1, user.getHistory().size());
        assertEquals("first", user.getHistory().get(0).getSong().getName());
    }

    @Test
    void nextAndPreviousHandleCurrentSongOutsideList() throws Exception {
        Song first = song("first");
        Song second = song("second");
        Playlist playlist = new Playlist(premium("creator"), "p", "desc", 0, "public", List.of(first, second));
        User user = premium("listener");

        playlist.setCurrentSong(song("external"));
        playlist.next(user);
        assertEquals("first", playlist.getCurrentSong().getName());

        playlist.setCurrentSong(song("external"));
        playlist.previous(user);
        assertEquals("second", playlist.getCurrentSong().getName());
    }

    @Test
    void navigationDoesNothingForNullEmptyAndSingleSongCases() {
        Playlist empty = new Playlist(premium("creator"), "empty", "desc", 0, "public", List.of());
        empty.next(premium("u"));
        empty.next(null);
        assertNull(empty.getCurrentSong());

        Song only = song("only");
        Playlist single = new Playlist(premium("creator"), "single", "desc", 0, "public", List.of(only));
        single.setCurrentSong(null);
        single.next(free("free"));
        assertNull(single.getCurrentSong());

        single.nextShuffle();
        assertEquals("only", single.getCurrentSong().getName());

        single.next(free("free"));
        assertEquals("only", single.getCurrentSong().getName());
    }

    @Test
    void addAndDeleteSongUpdateCurrentSongAndRejectInvalidState() throws Exception {
        User creator = premium("creator");
        Song first = song("first");
        Song second = song("second");
        Playlist playlist = new Playlist(creator, "p", "desc", 0, "public", new ArrayList<>());

        assertThrows(NullPointerException.class, () -> playlist.addSong(null));
        playlist.addSong(first);
        assertEquals("first", playlist.getCurrentSong().getName());
        playlist.addSong(second);

        playlist.deleteSong(first);
        assertEquals("second", playlist.getCurrentSong().getName());
        playlist.deleteSong(second);
        assertNull(playlist.getCurrentSong());
        assertThrows(NullPointerException.class, () -> playlist.deleteSong(null));
    }

    @Test
    void setSongsWithNullAndEmptyClearsCurrentSong() {
        Song first = song("first");
        Playlist playlist = new Playlist(premium("creator"), "p", "desc", 0, "public", List.of(first));

        playlist.setSongs(null);
        assertTrue(playlist.getSongs().isEmpty());
        assertNull(playlist.getCurrentSong());

        playlist.setSongs(List.of(first));
        assertEquals("first", playlist.getCurrentSong().getName());

        playlist.setSongs(List.of());
        assertTrue(playlist.getSongs().isEmpty());
        assertNull(playlist.getCurrentSong());
    }

    @Test
    void toStringIncludesNullCreatorEmptySongsAndNullCurrentSong() {
        Playlist playlist = new Playlist(null, "p", "desc", 0, "private", List.of());

        String text = playlist.toString();

        assertTrue(text.contains("creator: {null}"));
        assertTrue(text.contains("songs=[]"));
        assertTrue(text.contains("currentsong: {null}"));
    }

    @Test
    void freeNavigationAndShuffleWithTwoSongsAlwaysMoveAwayFromCurrentSong() {
        Song first = song("first");
        Song second = song("second");
        Playlist playlist = new Playlist(premium("creator"), "p", "desc", 0, "public", List.of(first, second));

        for (int i = 0; i < 20; i++) {
            playlist.setCurrentSong(first);
            playlist.next(free("free-" + i));
            assertEquals("second", playlist.getCurrentSong().getName());

            playlist.setCurrentSong(first);
            playlist.nextShuffle();
            assertEquals("second", playlist.getCurrentSong().getName());
        }
    }

    @Test
    void toStringIncludesNonEmptySongList() {
        Song first = song("first");
        Playlist playlist = new Playlist(premium("creator"), "p", "desc", 0, "private", List.of(first));

        assertTrue(playlist.toString().contains("first"));
    }

    @Test
    void toStringFallsBackToEmptyListWhenInternalSongsReferenceIsNull() throws Exception {
        Playlist playlist = new Playlist(premium("creator"), "p", "desc", 0, "private", List.of(song("first")));
        Field songsField = Playlist.class.getDeclaredField("songs");
        songsField.setAccessible(true);
        songsField.set(playlist, null);

        assertTrue(playlist.toString().contains("songs=[]"));
    }

    @Test
    void previousRejectsFreeUserButReturnsForNullUser() throws Exception {
        Playlist playlist = new Playlist(premium("creator"), "p", "desc", 0, "public", List.of(song("first")));

        playlist.previous(null);
        assertThrows(SubscriptionDoesNotAllowException.class, () -> playlist.previous(free("free")));
    }
}
