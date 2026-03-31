package org.spotifumtp37.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.spotifumtp37.model.album.ExplicitSong;
import org.spotifumtp37.model.album.MultimediaSong;
import org.spotifumtp37.model.album.Song;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SongTypeAdapterTest {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Song.class, new SongTypeAdapter())
            .create();

    @Test
    void deserializeShouldCreateMultimediaSong() {
        String json = "{" +
                "\"name\":\"video\"," +
                "\"artist\":\"a\"," +
                "\"publisher\":\"p\"," +
                "\"lyrics\":\"l\"," +
                "\"musicalNotes\":\"n\"," +
                "\"genre\":\"g\"," +
                "\"durationInSeconds\":120," +
                "\"multimedia\":true," +
                "\"videoLink\":\"https://x\"" +
                "}";

        Song parsed = gson.fromJson(json, Song.class);

        assertInstanceOf(MultimediaSong.class, parsed);
        assertEquals("https://x", ((MultimediaSong) parsed).getVideoLink());
    }

    @Test
    void deserializeShouldCreateExplicitSong() {
        String json = "{" +
                "\"name\":\"explicit\"," +
                "\"artist\":\"a\"," +
                "\"publisher\":\"p\"," +
                "\"lyrics\":\"l\"," +
                "\"musicalNotes\":\"n\"," +
                "\"genre\":\"g\"," +
                "\"durationInSeconds\":100," +
                "\"explicit\":true" +
                "}";

        Song parsed = gson.fromJson(json, Song.class);

        assertInstanceOf(ExplicitSong.class, parsed);
    }

    @Test
    void serializeShouldMarkExplicitSong() {
        Song explicit = new ExplicitSong("s", "a", "p", "l", "n", "g", 99);

        JsonObject obj = JsonParser.parseString(gson.toJson(explicit, Song.class)).getAsJsonObject();

        assertTrue(obj.has("explicit"));
        assertTrue(obj.get("explicit").getAsBoolean());
    }

    @Test
    void serializeShouldMarkMultimediaSongAndKeepVideoLink() {
        String json = "{" +
                "\"name\":\"video\"," +
                "\"artist\":\"a\"," +
                "\"publisher\":\"p\"," +
                "\"lyrics\":\"l\"," +
                "\"musicalNotes\":\"n\"," +
                "\"genre\":\"g\"," +
                "\"durationInSeconds\":120," +
                "\"multimedia\":true," +
                "\"videoLink\":\"https://x\"" +
                "}";
        Song multimedia = gson.fromJson(json, Song.class);

        JsonObject obj = JsonParser.parseString(gson.toJson(multimedia, Song.class)).getAsJsonObject();

        assertTrue(obj.has("multimedia"));
        assertTrue(obj.get("multimedia").getAsBoolean());
        assertEquals("https://x", obj.get("videoLink").getAsString());
    }
}
