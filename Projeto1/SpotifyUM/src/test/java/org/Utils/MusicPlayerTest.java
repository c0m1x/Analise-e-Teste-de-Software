package org.Utils;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MusicPlayerTest {

    @Test
    void playMusic_nonexistentResource_returnsNull() {
        MusicPlayer player = new MusicPlayer();
        assertNull(player.playMusic("this_song_does_not_exist_12345"));
    }

    @Test
    void playMusic_blankName_returnsNull() {
        MusicPlayer player = new MusicPlayer();
        assertNull(player.playMusic("   "));
    }

    @Test
    void playMusic_invalidAudioResourceReportsErrorAndReturnsNull() {
        MusicPlayer player = new MusicPlayer();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        try {
            System.setOut(new PrintStream(out));
            System.setErr(new PrintStream(err));

            assertNull(player.playMusic("Broken"));

            assertTrue(out.toString().contains("Erro ao reproduzir áudio"));
            assertFalse(err.toString().isEmpty());
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
}
