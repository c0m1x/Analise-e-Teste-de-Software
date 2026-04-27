package org.PropertyBased;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.constraints.IntRange;

import org.Exceptions.AlreadyExistsException;
import org.Exceptions.NotFoundException;
import org.Model.SpotifUM;
import org.Model.Music.Music;
import org.Model.Plan.PlanFree;
import org.Model.Plan.PlanPremiumBase;
import org.Model.Plan.PlanPremiumTop;

import static org.junit.jupiter.api.Assertions.*;

public class SpotifUMPropertyBasedTest {

    @Provide
    Arbitrary<String> simpleNames() {
        return Arbitraries.strings()
            .withCharRange('a', 'z')
            .ofMinLength(1)
            .ofMaxLength(18);
    }

    @Provide
    Arbitrary<String> urls() {
        return Arbitraries.strings()
            .withCharRange('a', 'z')
            .ofMinLength(1)
            .ofMaxLength(12)
            .map(s -> "http://example.com/" + s);
    }

    @Property(tries = 400)
    void planPointsNeverDecrease_Free(@ForAll @IntRange(min = 0, max = 200) int addCalls) {
        PlanFree plan = new PlanFree();
        int before = plan.getPoints();

        for (int i = 0; i < addCalls; i++) {
            plan.addPoints();
            assertTrue(plan.getPoints() >= before);
            before = plan.getPoints();
        }

        assertFalse(plan.canAccessLibrary());
        assertFalse(plan.canSkip());
        assertFalse(plan.canChooseWhatToPlay());
        assertFalse(plan.hasAccessToFavorites());
        assertEquals("Free", plan.getPlanName());
    }

    @Property(tries = 400)
    void planPointsNeverDecrease_PremiumBase(@ForAll @IntRange(min = 0, max = 200) int addCalls) {
        PlanPremiumBase plan = new PlanPremiumBase();
        int before = plan.getPoints();

        for (int i = 0; i < addCalls; i++) {
            plan.addPoints();
            assertTrue(plan.getPoints() >= before);
            before = plan.getPoints();
        }

        assertTrue(plan.canAccessLibrary());
        assertTrue(plan.canSkip());
        assertTrue(plan.canChooseWhatToPlay());
        assertFalse(plan.hasAccessToFavorites());
        assertEquals("PremiumBase", plan.getPlanName());
    }

    @Property(tries = 400)
    void planPointsNeverDecrease_PremiumTop(@ForAll @IntRange(min = 0, max = 200) int addCalls) {
        PlanPremiumTop plan = new PlanPremiumTop();
        int before = plan.getPoints();

        for (int i = 0; i < addCalls; i++) {
            plan.addPoints();
            assertTrue(plan.getPoints() >= before);
            before = plan.getPoints();
        }

        assertTrue(plan.canAccessLibrary());
        assertTrue(plan.canSkip());
        assertTrue(plan.canChooseWhatToPlay());
        assertTrue(plan.hasAccessToFavorites());
        assertEquals("PremiumTop", plan.getPlanName());
    }

    @Property(tries = 300)
    void addingSameMusicTwiceThrowsAlreadyExists(
        @ForAll("simpleNames") String albumName,
        @ForAll("simpleNames") String artistName,
        @ForAll("simpleNames") String musicName,
        @ForAll @IntRange(min = 0, max = 60 * 60) int duration,
        @ForAll boolean explicit,
        @ForAll boolean withUrl,
        @ForAll("urls") String url
    ) {
        SpotifUM model = new SpotifUM();
        model.addNewAlbum(albumName, artistName);

        String effectiveUrl = withUrl ? url : null;

        assertDoesNotThrow(() ->
            model.addNewMusic(
                musicName,
                artistName,
                "publisher",
                "lyrics",
                "figures",
                "genre",
                albumName,
                duration,
                explicit,
                effectiveUrl
            )
        );

        assertThrows(AlreadyExistsException.class, () ->
            model.addNewMusic(
                musicName,
                artistName,
                "publisher",
                "lyrics",
                "figures",
                "genre",
                albumName,
                duration,
                explicit,
                effectiveUrl
            )
        );

        assertTrue(model.musicExists(musicName));
    }

    @Property(tries = 250)
    void playingMusicNTimesIncrementsReproductions(
        @ForAll("simpleNames") String albumName,
        @ForAll("simpleNames") String artistName,
        @ForAll("simpleNames") String musicName,
        @ForAll @IntRange(min = 0, max = 200) int plays
    ) throws AlreadyExistsException, NotFoundException {
        SpotifUM model = new SpotifUM();
        model.addNewAlbum(albumName, artistName);
        model.addNewMusic(
            musicName,
            artistName,
            "publisher",
            "lyrics",
            "figures",
            "genre",
            albumName,
            120,
            false,
            null
        );

        for (int i = 0; i < plays; i++) {
            model.playMusic(musicName);
        }

        Music after = model.getMusicByName(musicName);
        assertEquals(plays, after.getReproductions());
    }

    @Property(tries = 250)
    void userCanAuthenticateAfterRegistration(
        @ForAll("simpleNames") String username,
        @ForAll("simpleNames") String password
    ) throws Exception {
        SpotifUM model = new SpotifUM();
        model.addNewUser(username, username + "@mail.com", "addr", password);

        model.authenticateUser(username, password);

        assertNotNull(model.getCurrentUser());
        assertEquals(username, model.getCurrentUser().getUsername());
        assertTrue(model.isPasswordCorrect(password));
    }
}
