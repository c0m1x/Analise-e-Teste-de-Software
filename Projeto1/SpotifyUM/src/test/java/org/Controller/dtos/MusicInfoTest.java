package org.Controller.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class MusicInfoTest {

    @Test
    void ctor_parsesLyricsAndStoresFields() {
        MusicInfo info = new MusicInfo("Song", "Artist", "a b  c", "url", true);
        assertEquals("Song", info.getMusicName());
        assertEquals("Artist", info.getArtistName());
        assertTrue(info.isExplicit());
        assertEquals("url", info.getUrl());
        assertNotNull(info.getLyrics());
        assertTrue(info.getLyrics().length >= 2);
        assertEquals("", info.getErrorMessage());
    }

    @Test
    void ctor_errorMessage_setsErrorMode() {
        MusicInfo err = new MusicInfo("boom");
        assertEquals("", err.getMusicName());
        assertEquals("", err.getArtistName());
        assertNull(err.getLyrics());
        assertFalse(err.isExplicit());
        assertEquals("", err.getUrl());
        assertEquals("boom", err.getErrorMessage());
        err.setErrorMessage("changed");
        assertEquals("changed", err.getErrorMessage());
    }
}
