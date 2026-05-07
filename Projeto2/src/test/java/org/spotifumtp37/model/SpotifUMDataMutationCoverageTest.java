package org.spotifumtp37.model;

import org.junit.jupiter.api.Test;
import org.spotifumtp37.exceptions.DoesntExistException;
import org.spotifumtp37.model.album.Album;
import org.spotifumtp37.model.album.Song;
import org.spotifumtp37.model.playlist.Playlist;
import org.spotifumtp37.model.subscription.FreePlan;
import org.spotifumtp37.model.subscription.PremiumBase;
import org.spotifumtp37.model.user.History;
import org.spotifumtp37.model.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SpotifUMDataMutationCoverageTest {

    private static Song song(String name) {
        return new Song(name, "artist", "publisher", "lyrics", "notes", "rock", 120);
    }

    private static User user(String name) {
        return new User(name, name + "@mail.com", "addr", new PremiumBase(), "pw", 0, new ArrayList<History>());
    }

    @Test
    void equalsExercisesAllBranchesAndFields() {
        SpotifUMData empty = new SpotifUMData();
        SpotifUMData same = new SpotifUMData();

        assertEquals(empty, empty);
        assertEquals(empty, same);
        assertNotEquals(empty, null);
        assertNotEquals(empty, "data");

        SpotifUMData withAlbum = new SpotifUMData();
        withAlbum.setMapAlbums(Map.of("album", new Album("album", "artist", 2024, "rock", List.of(song("s")))));
        assertNotEquals(empty, withAlbum);

        SpotifUMData withUser = new SpotifUMData();
        withUser.setMapUsers(Map.of("u", user("u")));
        assertNotEquals(empty, withUser);

        SpotifUMData withPlaylist = new SpotifUMData();
        User creator = user("creator");
        withPlaylist.setMapPlaylists(Map.of("p", new Playlist(creator, "p", "desc", 0, "private", List.of(song("s")))));
        assertNotEquals(empty, withPlaylist);
    }

    @Test
    void existsSongAndGetSongCoverMissingAlbumMissingSongAndPresentSong() throws Exception {
        SpotifUMData data = new SpotifUMData();
        data.addAlbum(new Album("album", "artist", 2024, "rock", List.of(song("present"))));

        assertTrue(data.existsSong("present", "album"));
        assertFalse(data.existsSong("missing", "album"));
        assertFalse(data.existsSong("present", "missing-album"));
        assertEquals("present", data.getSong("present", "album").getName());
        assertThrows(DoesntExistException.class, () -> data.getSong("missing", "album"));
    }

    @Test
    void playlistMapByCreatorSeparatesCreators() throws Exception {
        SpotifUMData data = new SpotifUMData();
        User creator = user("creator");
        User other = new User("other", "other@mail.com", "addr", new FreePlan(), "pw", 0, new ArrayList<History>());
        Song s = song("s");

        data.addPlaylist(new Playlist(creator, "mine", "desc", 0, "private", List.of(s)));
        data.addPlaylist(new Playlist(other, "theirs", "desc", 0, "private", List.of(s)));

        Map<String, Playlist> mine = data.getPlaylistMapByCreator(creator);
        assertEquals(1, mine.size());
        assertTrue(mine.containsKey("mine"));
        assertFalse(mine.containsKey("theirs"));
    }
}
