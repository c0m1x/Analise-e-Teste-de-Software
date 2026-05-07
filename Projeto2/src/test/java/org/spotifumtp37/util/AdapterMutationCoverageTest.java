package org.spotifumtp37.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.spotifumtp37.model.album.Album;
import org.spotifumtp37.model.album.ExplicitSong;
import org.spotifumtp37.model.album.MultimediaSong;
import org.spotifumtp37.model.album.Song;
import org.spotifumtp37.model.subscription.FreePlan;
import org.spotifumtp37.model.subscription.PremiumBase;
import org.spotifumtp37.model.subscription.PremiumTop;
import org.spotifumtp37.model.subscription.SubscriptionPlan;

import static org.junit.jupiter.api.Assertions.*;

class AdapterMutationCoverageTest {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Song.class, new SongTypeAdapter())
            .registerTypeAdapter(SubscriptionPlan.class, new SubscriptionPlanAdapter())
            .create();

    @Test
    void songAdapterRoundTripsRegularExplicitAndMultimediaMetadata() {
        Song regular = new Song("regular", "artist", "publisher", "lyrics", "notes", "rock", 120);
        ExplicitSong explicit = new ExplicitSong("explicit", "artist", "publisher", "lyrics", "notes", "rock", 121);
        Album album = new Album("album", "artist", 2024, "rock", java.util.List.of());
        album.addSong("video", "publisher", "lyrics", "notes", "rock", 122, false, true, "http://video");
        Song multimedia = album.getSongs().get(0);

        String regularJson = gson.toJson(regular, Song.class);
        assertFalse(regularJson.contains("explicit"));
        assertFalse(regularJson.contains("multimedia"));

        String explicitJson = gson.toJson(explicit, Song.class);
        assertTrue(explicitJson.contains("\"explicit\":true"));
        Song explicitBack = gson.fromJson(explicitJson, Song.class);
        assertInstanceOf(ExplicitSong.class, explicitBack);
        assertTrue(explicitBack.isExplicit());

        String multimediaJson = gson.toJson(multimedia, Song.class);
        assertTrue(multimediaJson.contains("\"multimedia\":true"));
        assertTrue(multimediaJson.contains("\"videoLink\":\"http://video\""));
        Song multimediaBack = gson.fromJson(multimediaJson, Song.class);
        assertInstanceOf(MultimediaSong.class, multimediaBack);
        assertTrue(multimediaBack.isMultimedia());
        assertEquals("http://video", ((MultimediaSong) multimediaBack).getVideoLink());
    }

    @Test
    void subscriptionAdapterDeserializesAllKnownTypesAndDefaultsToFree() {
        assertInstanceOf(PremiumBase.class, gson.fromJson("{\"type\":\"PremiumBase\"}", SubscriptionPlan.class));
        assertInstanceOf(PremiumTop.class, gson.fromJson("{\"type\":\"PremiumTop\"}", SubscriptionPlan.class));
        assertInstanceOf(FreePlan.class, gson.fromJson("{\"type\":\"unknown\"}", SubscriptionPlan.class));
        assertInstanceOf(FreePlan.class, gson.fromJson("{}", SubscriptionPlan.class));
    }
}
