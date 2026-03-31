package org.spotifumtp37.util;

import org.junit.jupiter.api.Test;
import org.spotifumtp37.model.SpotifUMData;
import org.spotifumtp37.model.album.Album;
import org.spotifumtp37.model.album.Song;
import org.spotifumtp37.model.playlist.Playlist;
import org.spotifumtp37.model.subscription.PremiumBase;
import org.spotifumtp37.model.user.History;
import org.spotifumtp37.model.user.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonDataParserTest {

    @Test
    void shouldRoundTripSingleFile() throws Exception {
        JsonDataParser parser = new JsonDataParser();
        SpotifUMData original = buildData();

        Path tempFile = Files.createTempFile("spotifum-data", ".json");
        try {
            parser.toJsonData(original, tempFile.toString());
            SpotifUMData loaded = parser.fromJsonData(tempFile.toString());

            assertEquals(1, loaded.getMapUsers().size());
            assertEquals(1, loaded.getMapAlbums().size());
            assertEquals(1, loaded.getMapPlaylists().size());
            assertTrue(loaded.existsUser("user1"));
            assertTrue(loaded.existsAlbum("album1"));
            assertTrue(loaded.existsPlaylist("playlist1"));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void shouldRoundTripDirectoryMode() throws Exception {
        JsonDataParser parser = new JsonDataParser();
        SpotifUMData original = buildData();

        Path tempDir = Files.createTempDirectory("spotifum-split");
        try {
            parser.toJsonData(original, tempDir.toString());
            SpotifUMData loaded = parser.fromJsonData(tempDir.toString());

            assertEquals(1, loaded.getMapUsers().size());
            assertEquals(1, loaded.getMapAlbums().size());
            assertEquals(1, loaded.getMapPlaylists().size());
            assertTrue(Files.exists(tempDir.resolve("users.json")));
            assertTrue(Files.exists(tempDir.resolve("albums.json")));
            assertTrue(Files.exists(tempDir.resolve("playlists.json")));
        } finally {
            Files.walk(tempDir)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (Exception ignored) {
                        }
                    });
        }
    }

    private SpotifUMData buildData() {
        Song song = new Song("song1", "artist1", "publisher1", "lyrics", "notes", "rock", 180);
        Album album = new Album("album1", "artist1", 2020, "rock", List.of(song));

        User user = new User(
                "user1",
                "user1@mail.com",
                "street",
                new PremiumBase(),
                "pw",
                0,
                new ArrayList<History>()
        );

        Playlist playlist = new Playlist(user, "playlist1", "desc", 0, "public", List.of(song));

        SpotifUMData data = new SpotifUMData();
        data.setMapUsers(Map.of(user.getName(), user));
        data.setMapAlbums(Map.of(album.getTitle(), album));
        data.setMapPlaylists(Map.of(playlist.getPlaylistName(), playlist));
        return data;
    }
}
