package org.Utils;

import org.junit.jupiter.api.Test;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class BrowserOpenerTest {

    @Test
    void abrir_invalidUrl_throwsURISyntaxException() {
        BrowserOpener opener = new BrowserOpener("ht!tp://bad url");
        assertThrows(URISyntaxException.class, opener::abrir);
    }

    @Test
    void abrir_whenDesktopIsUnavailableThrowsUnsupportedOperationException() {
        assumeFalse(Desktop.isDesktopSupported());
        BrowserOpener opener = new BrowserOpener("https://example.com");

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, opener::abrir);
        assertEquals("Desktop não suportado.", exception.getMessage());
    }
}
