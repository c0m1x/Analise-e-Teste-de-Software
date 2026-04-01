package org.Utils;

import org.junit.jupiter.api.Test;

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
}
