package org.spotifumtp37.util;

import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalDateTimeAdapterTest {

    private final LocalDateTimeAdapter adapter = new LocalDateTimeAdapter();

    @Test
    void serializeShouldUseIsoFormat() {
        LocalDateTime value = LocalDateTime.of(2026, 3, 31, 21, 45, 30);

        String serialized = adapter.serialize(value, LocalDateTime.class, null).getAsString();

        assertEquals("2026-03-31T21:45:30", serialized);
    }

    @Test
    void deserializeShouldParseIsoFormat() {
        LocalDateTime parsed = adapter.deserialize(new JsonPrimitive("2026-03-31T21:45:30"), LocalDateTime.class, null);

        assertEquals(LocalDateTime.of(2026, 3, 31, 21, 45, 30), parsed);
    }

    @Test
    void deserializeShouldFailForInvalidFormat() {
        assertThrows(DateTimeParseException.class,
                () -> adapter.deserialize(new JsonPrimitive("31-03-2026 21:45:30"), LocalDateTime.class, null));
    }
}
