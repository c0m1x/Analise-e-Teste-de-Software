package org.Utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.Serializable;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SerializationTest {

    private static final class Dummy implements Serializable {
        private final String value;

        private Dummy(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Dummy dummy = (Dummy) o;
            return value.equals(dummy.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    @Test
    void exportar_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Serialization.exportar(null, "x.ser"));
    }

    @Test
    void exportar_importar_roundTrip(@TempDir Path tempDir) {
        Path file = tempDir.resolve("dummy.ser");
        Dummy original = new Dummy("hello");

        Serialization.exportar(original, file.toString());
        Dummy loaded = Serialization.importar(file.toString());

        assertEquals(original, loaded);
    }

    @Test
    void importar_missingFile_throwsRuntimeException(@TempDir Path tempDir) {
        Path missing = tempDir.resolve("missing.ser");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> Serialization.importar(missing.toString()));
        assertTrue(ex.getMessage().contains("Erro ao importar o objeto"));
    }

    @Test
    void exportar_invalidPath_throwsRuntimeException(@TempDir Path tempDir) {
        Path invalid = tempDir.resolve("no_such_dir").resolve("dummy.ser");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> Serialization.exportar(new Dummy("x"), invalid.toString()));
        assertTrue(ex.getMessage().contains("Erro ao exportar o objeto"));
    }
}
