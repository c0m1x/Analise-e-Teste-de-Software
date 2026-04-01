package org.Utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class BrowserOpenerTest {

    @Test
    void abrir_invalidUrl_throwsURISyntaxException() {
        BrowserOpener opener = new BrowserOpener("ht!tp://bad url");
        assertThrows(URISyntaxException.class, opener::abrir);
    }
}
