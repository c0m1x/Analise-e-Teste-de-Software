package org.spotifumtp37.model.playlist;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import org.spotifumtp37.model.album.Song;
import org.spotifumtp37.model.subscription.PremiumBase;
import org.spotifumtp37.model.user.History;
import org.spotifumtp37.model.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PlaylistPropertyTest {

    @Property
    void setSongsShouldReplaceAllSongs(@ForAll("songLists") List<Song> songs) {
        Playlist playlist = basePlaylist();

        playlist.setSongs(songs);

        List<Song> storedSongs = playlist.getSongs();
        assertEquals(songs.size(), storedSongs.size());
        assertEquals(
                songs.stream().map(Song::getName).collect(Collectors.toList()),
                storedSongs.stream().map(Song::getName).collect(Collectors.toList())
        );
    }

    @Property
    void getSongsShouldReturnDefensiveCopies(@ForAll("nonEmptySongLists") List<Song> songs) {
        Playlist playlist = basePlaylist();
        playlist.setSongs(songs);

        List<Song> exposedList = playlist.getSongs();
        String originalFirstName = playlist.getSongs().get(0).getName();

        exposedList.get(0).setName("mutated-name");
        exposedList.clear();

        List<Song> afterMutation = playlist.getSongs();
        assertNotEquals("mutated-name", afterMutation.get(0).getName());
        assertEquals(originalFirstName, afterMutation.get(0).getName());
        assertEquals(songs.size(), afterMutation.size());
    }

    @Provide
    @SuppressWarnings("unused")
    Arbitrary<List<Song>> songLists() {
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(1)
                .ofMaxLength(20)
                .list()
                .ofMinSize(0)
                .ofMaxSize(8)
                .map(this::toSongs);
    }

    @Provide
    @SuppressWarnings("unused")
    Arbitrary<List<Song>> nonEmptySongLists() {
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(1)
                .ofMaxLength(20)
                .list()
                .ofMinSize(1)
                .ofMaxSize(8)
                .map(this::toSongs);
    }

    private List<Song> toSongs(List<String> names) {
        List<Song> songs = new ArrayList<>();
        int i = 0;
        for (String name : names) {
            songs.add(new Song(name, "artist" + i, "publisher" + i, "lyrics", "notes", "genre", 120 + i));
            i++;
        }
        return songs;
    }

    private Playlist basePlaylist() {
        User creator = new User("creator", "creator@mail.com", "addr", new PremiumBase(), "pw", 0, new ArrayList<History>());
        List<Song> seedSongs = List.of(
                new Song("seed1", "artist1", "publisher1", "lyrics1", "notes1", "genre1", 100),
                new Song("seed2", "artist2", "publisher2", "lyrics2", "notes2", "genre2", 110),
                new Song("seed3", "artist3", "publisher3", "lyrics3", "notes3", "genre3", 120)
        );
        return new Playlist(creator, "p", "d", 0, "public", seedSongs);
    }
}
