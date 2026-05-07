package org.Model.User;

import org.Model.Music.Music;
import org.Model.Plan.PlanPremiumBase;
import org.Model.Playlist.Playlist;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMutationCoverageTest {

    private static Music song(String name) {
        return new Music(name, "artist", "publisher", "lyrics", "notes", "rock", "album", 120, false);
    }

    @Test
    void equalsChecksEveryProfileFieldAndType() {
        User base = new User("u", "u@mail.com", "addr", "pw");

        assertEquals(base, base);
        assertEquals(base, new User("u", "u@mail.com", "addr", "pw"));
        assertNotEquals(base, null);
        assertNotEquals(base, "user");
        assertNotEquals(base, new User("other", "u@mail.com", "addr", "pw"));
        assertNotEquals(base, new User("u", "other@mail.com", "addr", "pw"));
        assertNotEquals(base, new User("u", "u@mail.com", "other", "pw"));
        assertNotEquals(base, new User("u", "u@mail.com", "addr", "other"));
    }

    @Test
    void playlistAuthorChangeAndCountOnlyOwnPlaylists() throws Exception {
        User user = new User("u", "u@mail.com", "addr", "pw");
        user.setPlan(new PlanPremiumBase());
        Playlist own = new Playlist("own", "u");
        Playlist foreign = new Playlist("foreign", "someone");

        user.setPlaylists(new ArrayList<>(List.of(own, foreign)));

        assertEquals(1, user.getUserPlaylistCount());
        user.changePlaylistAutor("renamed");

        List<Playlist> playlists = user.getPlaylists();
        assertEquals(1, playlists.stream().filter(p -> p.getAutor().equals("renamed")).count());
        assertEquals(1, playlists.stream().filter(p -> p.getAutor().equals("someone")).count());
    }

    @Test
    void musicReproductionCountUsesStrictDateBoundaries() {
        User user = new User("u", "u@mail.com", "addr", "pw");
        Music music = song("s");
        user.addMusicReproduction(music);

        assertEquals(1, user.getMusicReproductionsCount(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)));
        assertEquals(0, user.getMusicReproductionsCount(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)));
    }

    @Test
    void toStringContainsProfileDataAndMasksPassword() {
        User user = new User("u", "u@mail.com", "addr", "secret");

        String text = user.toString();

        assertTrue(text.contains("u"));
        assertTrue(text.contains("u@mail.com"));
        assertTrue(text.contains("addr"));
        assertFalse(text.contains("secret"));
    }
}
