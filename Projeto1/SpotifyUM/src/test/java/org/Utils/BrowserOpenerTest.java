package org.Utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class BrowserOpenerTest {

    @Test
    void abrir_invalidUrl_throwsURISyntaxException() {
        BrowserOpener opener = new BrowserOpener("ht!tp://bad url");
        assertThrows(URISyntaxException.class, opener::abrir);
    }

    @Test
    void abrir_whenDesktopIsUnavailableThrowsUnsupportedOperationException() {
        BrowserOpener opener = new BrowserOpener("https://example.com") {
            @Override
            protected boolean isDesktopSupported() {
                return false;
            }
        };

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, opener::abrir);
        assertEquals("Desktop não suportado.", exception.getMessage());
    }

    @Test
    void abrir_whenBrowseThrowsIOExceptionWrapsMessage() {
        BrowserOpener opener = new BrowserOpener("https://example.com") {
            @Override
            protected boolean isDesktopSupported() {
                return true;
            }

            @Override
            protected void browse(URI uri) throws IOException {
                throw new IOException("boom");
            }
        };

        IOException exception = assertThrows(IOException.class, opener::abrir);
        assertTrue(exception.getMessage().startsWith("Erro ao tentar abrir o URL: https://example.com"));
        assertNotNull(exception.getCause());
        assertEquals("boom", exception.getCause().getMessage());
    }
}
