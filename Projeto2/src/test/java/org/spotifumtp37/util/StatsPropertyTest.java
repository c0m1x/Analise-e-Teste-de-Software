package org.spotifumtp37.util;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import org.spotifumtp37.model.album.Song;
import org.spotifumtp37.model.playlist.Playlist;
import org.spotifumtp37.model.subscription.PremiumBase;
import org.spotifumtp37.model.user.History;
import org.spotifumtp37.model.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatsPropertyTest {

    @Property
    void countPublicPlaylistsShouldMatchPublicFlagCount(@ForAll("visibilityFlags") List<Boolean> visibilityFlags) {
        User creator = new User("creator", "creator@mail.com", "addr", new PremiumBase(), "pw", 0, new ArrayList<History>());
        Song seed = new Song("seed", "artist", "publisher", "lyrics", "notes", "genre", 100);
        List<Song> songs = List.of(seed);

        Map<String, Playlist> playlists = new HashMap<>();
        int expectedPublic = 0;

        for (int i = 0; i < visibilityFlags.size(); i++) {
            boolean isPublic = visibilityFlags.get(i);
            if (isPublic) {
                expectedPublic++;
            }
            playlists.put("p" + i, new Playlist(creator, "p" + i, "d", 0, isPublic ? "public" : "private", songs));
        }

        assertEquals(expectedPublic, Stats.countPublicPlaylists(playlists));
    }

    @Provide
    Arbitrary<List<Boolean>> visibilityFlags() {
        return Arbitraries.of(true, false).list().ofMinSize(0).ofMaxSize(20);
    }
}
