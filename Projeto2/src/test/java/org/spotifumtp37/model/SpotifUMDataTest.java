package org.spotifumtp37.model;

import org.junit.jupiter.api.Test;
import org.spotifumtp37.exceptions.AlreadyExistsException;
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

class SpotifUMDataTest {

    @Test
    void shouldAddRetrieveAndRemoveCoreEntities() throws Exception {
        SpotifUMData data = new SpotifUMData();
        Song song = new Song("song-1", "artist", "publisher", "lyrics", "notes", "rock", 180);
        Album album = new Album("album-1", "artist", 2024, "rock", List.of(song));
        User creator = new User("creator", "creator@mail.com", "street", new PremiumBase(), "pw", 7, new ArrayList<History>());
        Playlist playlist = new Playlist(creator, "playlist-1", "desc", 0, "private", List.of(song));

        data.addAlbum(album);
        data.addUser(creator);
        data.addPlaylist(playlist);

        assertTrue(data.existsAlbum("album-1"));
        assertTrue(data.existsUser("creator"));
        assertTrue(data.existsPlaylist("playlist-1"));
        assertTrue(data.existsSong("song-1", "album-1"));

        assertEquals("album-1", data.getAlbum("album-1").getTitle());
        assertEquals("playlist-1", data.getPlaylist("playlist-1").getPlaylistName());
        assertEquals("creator", data.getUser("creator").getName());
        assertNotSame(creator, data.getUser("creator"));
        assertEquals("creator", data.getCurrentUserPointer("creator").getName());

        assertEquals("song-1", data.getSong("song-1", "album-1").getName());
        assertEquals("playlist-1", data.getAnyPlaylist("playlist-1", creator).getPlaylistName());

        assertThrows(AlreadyExistsException.class, () -> data.addAlbum(album));
        assertThrows(AlreadyExistsException.class, () -> data.addUser(creator));
        assertThrows(AlreadyExistsException.class, () -> data.addPlaylist(playlist));

        data.removeAlbum("album-1");
        data.removeUser("creator");
        data.removePlaylist("playlist-1");

        assertFalse(data.existsAlbum("album-1"));
        assertFalse(data.existsUser("creator"));
        assertFalse(data.existsPlaylist("playlist-1"));

        assertThrows(DoesntExistException.class, () -> data.getAlbum("album-1"));
        assertThrows(DoesntExistException.class, () -> data.getUser("creator"));
        assertThrows(DoesntExistException.class, () -> data.getPlaylist("playlist-1"));
    }

    @Test
    void shouldCloneDefensivelyAcrossAllMaps() throws Exception {
        SpotifUMData data = buildSampleData();
        SpotifUMData clone = data.clone();

        data.removeAlbum("album-1");
        data.removeUser("creator");
        data.removePlaylist("playlist-1");

        assertTrue(clone.existsAlbum("album-1"));
        assertTrue(clone.existsUser("creator"));
        assertTrue(clone.existsPlaylist("playlist-1"));

        Map<String, Album> albumCopy = clone.getMapAlbumsCopy();
        albumCopy.clear();
        assertTrue(clone.existsAlbum("album-1"));

        Map<String, User> userCopy = clone.getMapUsers();
        userCopy.clear();
        assertTrue(clone.existsUser("creator"));

        Map<String, Playlist> playlistCopy = clone.getMapPlaylists();
        playlistCopy.clear();
        assertTrue(clone.existsPlaylist("playlist-1"));
    }

    @Test
    void shouldRespectPrivateAndPublicPlaylistAccessRules() throws Exception {
        SpotifUMData data = new SpotifUMData();
        Song song = new Song("song-1", "artist", "publisher", "lyrics", "notes", "rock", 180);
        User creator = new User("creator", "creator@mail.com", "street", new FreePlan(), "pw", 0, new ArrayList<History>());
        User otherUser = new User("other", "other@mail.com", "street", new FreePlan(), "pw", 0, new ArrayList<History>());

        Playlist privatePlaylist = new Playlist(creator, "private-playlist", "desc", 0, "private", List.of(song));
        Playlist publicPlaylist = new Playlist(creator, "public-playlist", "desc", 0, "public", List.of(song));

        data.addUser(creator);
        data.addUser(otherUser);
        data.addPlaylist(privatePlaylist);
        data.addPlaylist(publicPlaylist);

        assertEquals("private-playlist", data.getAnyPlaylist("private-playlist", creator).getPlaylistName());
        assertEquals("public-playlist", data.getAnyPlaylist("public-playlist", otherUser).getPlaylistName());
        assertThrows(DoesntExistException.class, () -> data.getAnyPlaylist("private-playlist", otherUser));
    }

    private SpotifUMData buildSampleData() throws Exception {
        SpotifUMData data = new SpotifUMData();
        Song song = new Song("song-1", "artist", "publisher", "lyrics", "notes", "rock", 180);
        Album album = new Album("album-1", "artist", 2024, "rock", List.of(song));
        User creator = new User("creator", "creator@mail.com", "street", new PremiumBase(), "pw", 7, new ArrayList<History>());
        creator.updateHistory(song);
        Playlist playlist = new Playlist(creator, "playlist-1", "desc", 0, "private", List.of(song));

        data.setMapAlbums(Map.of(album.getTitle(), album));
        data.setMapUsers(Map.of(creator.getName(), creator));
        data.setMapPlaylists(Map.of(playlist.getPlaylistName(), playlist));
        return data;
    }
}