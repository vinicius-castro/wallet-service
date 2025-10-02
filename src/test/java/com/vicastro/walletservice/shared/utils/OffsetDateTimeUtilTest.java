package com.vicastro.walletservice.shared.utils;

import com.vicastro.walletservice.shared.exception.InvalidDateException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OffsetDateTimeUtilTest {

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertNull(OffsetDateTimeUtil.parseToOffsetDateTime(null));
    }

    @Test
    void shouldReturnNullWhenInputIsEmpty() {
        assertNull(OffsetDateTimeUtil.parseToOffsetDateTime("   "));
    }

    @Test
    void shouldParseValidIsoOffsetDateTimeString() {
        String input = "2024-06-01T10:15:30+01:00";
        OffsetDateTime result = OffsetDateTimeUtil.parseToOffsetDateTime(input);
        assertEquals(OffsetDateTime.parse(input), result);
    }

    @Test
    void shouldParseDateStringToMidnightUtcWhenIsoOffsetDateTimeFails() {
        String input = "2024-06-01";
        OffsetDateTime result = OffsetDateTimeUtil.parseToOffsetDateTime(input);
        assertEquals(
                OffsetDateTime.of(
                        2024,
                        6,
                        1,
                        0,
                        0,
                        0,
                        0,
                        ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now())
                ), result);
    }

    @Test
    void shouldThrowInvalidDateExceptionForInvalidString() {
        assertThrows(InvalidDateException.class, () ->
                OffsetDateTimeUtil.parseToOffsetDateTime("invalid-date"));
    }
}