package org.Controller;

import org.Controller.dtos.MusicInfo;
import org.Model.SpotifUM;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ControllerPitSurvivorTest {

    private static SpotifUM seededModelWithUserAndOneSong() throws Exception {
        SpotifUM model = new SpotifUM();
        model.addNewUser("u", "u@example.com", "addr", "pw");
        model.authenticateUser("u", "pw");

        model.addNewAlbum("alb", "artist");
        model.addNewMusic(
                "song",
                "artist",
                "pub",
                "la la",
                "fig",
                "rock",
                "alb",
                120,
                false,
                null
        );
        return model;
    }

    @Test
    void currentUserHasLibraryReflectsPlan() throws Exception {
        SpotifUM model = seededModelWithUserAndOneSong();
        Controller controller = new Controller(model);

        // default plan should be free -> no library
        assertFalse(controller.currentUserHasLibrary());

        controller.setPremiumTopPlan();
        assertTrue(controller.currentUserHasLibrary());
    }

    @Test
    void playMusicAddsPointsAndUpdatesStats() throws Exception {
        SpotifUM model = seededModelWithUserAndOneSong();
        Controller controller = new Controller(model);
        controller.setPremiumTopPlan();

        int pointsBefore = model.getCurrentUser().getPlan().getPoints();

        MusicInfo info = controller.playMusic("song");
        assertEquals("", info.getErrorMessage(), "Expected successful play without error");
        assertEquals("song", info.getMusicName());
        assertEquals("artist", info.getArtistName());

        int pointsAfter = model.getCurrentUser().getPlan().getPoints();
        assertTrue(pointsAfter > pointsBefore, "Expected points to increase after playing music");

        assertEquals(1, model.getArtistReproductions().getOrDefault("artist", 0));
        assertEquals(1, model.getGenreReproductions().getOrDefault("rock", 0));
        assertEquals(1, model.getCurrentUser().getMusicReproductions().size());
    }

    @Test
    void statsStringsAreNotEmptyAndContainExpectedData() {
        SpotifUM model = new SpotifUM();
        Controller controller = new Controller(model);

        Map<String, Integer> artistRepro = new HashMap<>();
        artistRepro.put("A", 1);
        artistRepro.put("B", 2);
        model.setArtistReproductions(artistRepro);

        Map<String, Integer> genreRepro = new HashMap<>();
        genreRepro.put("Rock", 3);
        model.setGenreReproductions(genreRepro);

        String artistMsg = controller.getMostReproducedArtist();
        assertTrue(artistMsg.startsWith("🎤 Artista mais reproduzido: "));
        assertTrue(artistMsg.contains("B - 2"), "Expected artist list to include counts, got: " + artistMsg);

        String genreMsg = controller.getMostReproducedGenre();
        assertEquals("🎶 Género mais reproduzido: Rock", genreMsg);
    }
}
